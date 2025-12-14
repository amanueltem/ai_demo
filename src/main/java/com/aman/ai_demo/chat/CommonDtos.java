package com.aman.ai_demo.chat;

import java.util.List;

public class CommonDtos {
}
record Person(String fullName, String birthPlace,String nationality){
}
record ActorFilms(String fullName, List<String> movies) {}