package com.example.surveyanswer.survey.domain;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class DesignTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Design_id")
    private Long Design_id;

    @Column(name = "font")
    private String font;

    @Column(name = "font_size")
    private int fontSize;

    @Column(name = "back_color")
    private String backColor;

    @OneToOne(mappedBy = "designTemplate")
    private SurveyDocument surveyTemplate;

    @Builder
    public DesignTemplate(String font, int fontSize, String backColor) {
        this.font = font;
        this.fontSize = fontSize;
        this.backColor = backColor;
    }

    // Request -> Entity
    public static DesignTemplate designRequestToEntity(String font, int fontSize, String backColor) {
        return DesignTemplate.builder()
                .font(font)
                .fontSize(fontSize)
                .backColor(backColor)
                .build();
    }
}