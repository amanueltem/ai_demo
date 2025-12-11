from fastapi import FastAPI
from pydantic import BaseModel
import chromadb
import uuid

# Initialize ChromaDB with the new PersistentClient
client = chromadb.PersistentClient(path="./db_data")

# Get or create collection
collection = client.get_or_create_collection("documents")

app = FastAPI(title="ChromaDB RAG Service")

# Request/Response Models
class AddDocumentRequest(BaseModel):
    content: str

class QueryRequest(BaseModel):
    query: str
    top_k: int = 3

class DocumentResponse(BaseModel):
    id: str
    content: str
    distance: float

class QueryResponse(BaseModel):
    query: str
    results: list[DocumentResponse]

# Simple embedding generation (replace with proper model in production)
def generate_embedding(text: str) -> list[float]:
    """
    Simple placeholder for embedding generation.
    In production, use: sentence-transformers, OpenAI, HuggingFace, etc.
    Example with sentence-transformers:
    
    from sentence_transformers import SentenceTransformer
    model = SentenceTransformer('all-MiniLM-L6-v2')
    return model.encode(text).tolist()
    """
    # Placeholder: returns random embeddings for demonstration
    import random
    return [random.uniform(-1, 1) for _ in range(384)]  # 384-dim placeholder

@app.get("/")
def read_root():
    return {"message": "ChromaDB RAG Service is running", "status": "healthy"}

@app.get("/collection_info")
def get_collection_info():
    """Get information about the collection"""
    count = collection.count()
    return {
        "collection_name": "documents",
        "document_count": count,
        "persistence_path": "./db_data"
    }

@app.post("/add_document", status_code=201)
def add_document(request: AddDocumentRequest):
    """Add a document with auto-generated embedding"""
    if not request.content.strip():
        return {"error": "Content cannot be empty"}
    
    # Generate unique ID
    doc_id = str(uuid.uuid4())
    
    # Generate embedding
    embedding = generate_embedding(request.content)
    
    # Add to collection
    collection.add(
        documents=[request.content],
        embeddings=[embedding],
        ids=[doc_id]
    )
    
    return {
        "status": "success",
        "message": "Document added successfully",
        "id": doc_id,
        "content_length": len(request.content),
        "embedding_dimension": len(embedding)
    }

@app.post("/add_document_batch", status_code=201)
def add_document_batch(documents: list[str]):
    """Add multiple documents at once"""
    if not documents:
        return {"error": "No documents provided"}
    
    ids = [str(uuid.uuid4()) for _ in range(len(documents))]
    embeddings = [generate_embedding(doc) for doc in documents]
    
    collection.add(
        documents=documents,
        embeddings=embeddings,
        ids=ids
    )
    
    return {
        "status": "success",
        "message": f"{len(documents)} documents added",
        "ids": ids
    }

@app.post("/query")
def query_documents(request: QueryRequest):
    """Query for similar documents"""
    if not request.query.strip():
        return {"error": "Query cannot be empty"}
    
    # Generate embedding for the query
    query_embedding = generate_embedding(request.query)
    
    # Query the collection
    results = collection.query(
        query_embeddings=[query_embedding],
        n_results=request.top_k,
        include=["documents", "distances", "metadatas", "embeddings"]
    )
    
    # Format the response
    formatted_results = []
    if results['documents'] and results['documents'][0]:
        for i in range(len(results['documents'][0])):
            formatted_results.append({
                "id": results['ids'][0][i] if results['ids'] else f"result_{i}",
                "content": results['documents'][0][i],
                "distance": float(results['distances'][0][i]) if results['distances'] else 0.0,
                "similarity_score": 1 - float(results['distances'][0][i]) if results['distances'] else 1.0
            })
    
    return {
        "query": request.query,
        "top_k": request.top_k,
        "results": formatted_results,
        "result_count": len(formatted_results)
    }

@app.get("/get_all_documents")
def get_all_documents(limit: int = 100):
    """Get all documents from the collection (paginated)"""
    results = collection.get(
        limit=limit,
        include=["documents", "embeddings"]
    )
    
    documents = []
    if results['documents']:
        for i in range(len(results['documents'])):
            documents.append({
                "id": results['ids'][i],
                "content": results['documents'][i],
                "embedding_length": len(results['embeddings'][i]) if results['embeddings'] else 0
            })
    
    return {
        "total_retrieved": len(documents),
        "limit": limit,
        "documents": documents
    }

@app.delete("/delete_document/{doc_id}")
def delete_document(doc_id: str):
    """Delete a specific document by ID"""
    collection.delete(ids=[doc_id])
    return {
        "status": "success",
        "message": f"Document {doc_id} deleted"
    }

@app.delete("/clear_collection")
def clear_collection(confirm: bool = False):
    """Clear all documents from the collection (requires confirmation)"""
    if not confirm:
        return {
            "warning": "This will delete ALL documents. Add ?confirm=true to proceed",
            "document_count": collection.count()
        }
    
    count_before = collection.count()
    # Get all IDs and delete them
    all_docs = collection.get()
    if all_docs['ids']:
        collection.delete(ids=all_docs['ids'])
    
    return {
        "status": "success",
        "message": f"Collection cleared. Removed {count_before} documents.",
        "current_count": collection.count()
    }

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="127.0.0.1", port=8000)