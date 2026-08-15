package com.example.demo.service;

import com.example.demo.enums.ProgrammingLanguage;
import org.springframework.stereotype.Service;

@Service
public class Judge0LanguageService {

    public int getLanguageId(
            ProgrammingLanguage language
    ) {

        return switch (language) {

            case CPP -> 54;

            case JAVA -> 62;

            case PYTHON -> 71;
        };
    }
}