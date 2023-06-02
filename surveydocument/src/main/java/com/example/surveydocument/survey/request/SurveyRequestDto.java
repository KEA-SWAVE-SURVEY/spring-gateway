package com.example.surveydocument.survey.request;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SurveyRequestDto {
    String title;
    String description;
    int type;
    List<QuestionRequestDto> questionRequest;
    Boolean reliability;
    String font;
    int size;
    String backcolor;

    //    @ConstructorProperties({"title", "description", "type", "questionRequest"})
    @Builder
    public SurveyRequestDto(String title, String description, int type,Boolean reliability,String font, int size, String backcolor, List<QuestionRequestDto> questionRequest) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.reliability=reliability;
        this.font=font;
        this.backcolor=backcolor;
        this.size=size;
        this.questionRequest = questionRequest;
    }
}
