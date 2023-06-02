package com.example.surveyanswer.survey.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SurveyResponseDto {
    Long id;
    String title;
    String description;
    int type;
    List<QuestionResponseDto> questionResponse;
    String font;
    int size;
    String backcolor;
    Boolean reliability;

    //    @ConstructorProperties({"title", "description", "type", "questionResponse"})
    @Builder
    public SurveyResponseDto(String title, String description, int type,String font,int size,String backcolor,Boolean reliability, List<QuestionResponseDto> questionResponse) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.font=font;
        this.size=size;
        this.backcolor=backcolor;
        this.reliability=reliability;
        this.questionResponse = questionResponse;
    }
}
