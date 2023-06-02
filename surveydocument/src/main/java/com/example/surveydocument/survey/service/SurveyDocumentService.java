package com.example.surveydocument.survey.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.surveydocument.survey.exception.InvalidTokenException;
import com.example.surveydocument.survey.repository.choice.ChoiceRepository;
import com.example.surveydocument.survey.repository.questionDocument.QuestionDocumentRepository;
import com.example.surveydocument.survey.repository.survey.SurveyRepository;
import com.example.surveydocument.survey.repository.surveyDocument.SurveyDocumentRepository;
import com.example.surveydocument.survey.repository.wordCloud.WordCloudRepository;
import com.example.surveydocument.survey.request.*;
import com.example.surveydocument.survey.response.ChoiceDetailDto;
import com.example.surveydocument.survey.response.QuestionDetailDto;
import com.example.surveydocument.survey.response.SurveyDetailDto;
import com.example.surveydocument.survey.response.WordCloudDto;
import com.example.surveydocument.user.domain.User;
import com.example.surveydocument.user.service.UserService2;
import com.example.surveydocument.util.OAuth.JwtProperties;
import com.example.surveydocument.util.page.PageRequest;
import com.example.surveydocument.survey.domain.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
//import static com.example.surveyAnswer.util.SurveyTypeCheck.typeCheck;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyDocumentService {
    private final UserService2 userService;
    private final SurveyRepository surveyRepository;
    private final SurveyDocumentRepository surveyDocumentRepository;
    private final QuestionDocumentRepository questionDocumentRepository;
    private final ChoiceRepository choiceRepository;
    private final WordCloudRepository wordCloudRepository;

    private static String gateway="localhost:8080";
    Random random = new Random();
    private List<ReliabilityQuestion> questions;
    private int reliabilityquestionNumber;
    public ReliabilityQuestion reliabilityQuestion() throws JsonProcessingException {
        String jsonString = "{\"questionRequest\":[" +
                "{\"title\":\"이 문항에는 어느것도 아니다를 선택해주세요.\",\"type\":2,\"correct_answer\":\"어느것도 아니다.\",\"choiceList\":[{\"id\":1,\"choiceName\":\"매우 부정적이다.\"},{\"id\":2,\"choiceName\":\"약간 부정적이다.\"},{\"id\":3,\"choiceName\":\"어느것도 아니다.\"},{\"id\":4,\"choiceName\":\"약간 긍정적이다..\"},{\"id\":5,\"choiceName\":\"매우 긍정적이다.\"}]}," +
                "{\"title\":\"이 문항에는 매우 부정적이다를 선택해주세요.\",\"correct_answer\":\"매우 부정적이다.\",\"type\":2,\"choiceList\":[{\"id\":1,\"choiceName\":\"매우 부정적이다.\"},{\"id\":2,\"choiceName\":\"약간 부정적이다.\"},{\"id\":3,\"choiceName\":\"어느것도 아니다.\"},{\"id\":4,\"choiceName\":\"약간 긍정적이다..\"},{\"id\":5,\"choiceName\":\"매우 긍정적이다.\"}]}," +
                "{\"title\":\"이 문항에는 약간 부정적이다를 선택해주세요.\",\"correct_answer\":\"약간 부정적이다.\",\"type\":2,\"choiceList\":[{\"id\":1,\"choiceName\":\"매우 부정적이다.\"},{\"id\":2,\"choiceName\":\"약간 부정적이다.\"},{\"id\":3,\"choiceName\":\"어느것도 아니다.\"},{\"id\":4,\"choiceName\":\"약간 긍정적이다..\"},{\"id\":5,\"choiceName\":\"매우 긍정적이다.\"}]}," +
                "{\"title\":\"이 문항에는 약간 긍정적이다를 선택해주세요.\",\"correct_answer\":\"약간 긍정적이다.\",\"type\":2,\"choiceList\":[{\"id\":1,\"choiceName\":\"매우 부정적이다.\"},{\"id\":2,\"choiceName\":\"약간 부정적이다.\"},{\"id\":3,\"choiceName\":\"어느것도 아니다.\"},{\"id\":4,\"choiceName\":\"약간 긍정적이다..\"},{\"id\":5,\"choiceName\":\"매우 긍정적이다.\"}]}," +
                "{\"title\":\"이 문항에는 매우 긍정적이다를 선택해주세요.\",\"correct_answer\":\"매우 긍정적이다.\",\"type\":2,\"choiceList\":[{\"id\":1,\"choiceName\":\"매우 부정적이다.\"},{\"id\":2,\"choiceName\":\"약간 부정적이다.\"},{\"id\":3,\"choiceName\":\"어느것도 아니다.\"},{\"id\":4,\"choiceName\":\"약간 긍정적이다..\"},{\"id\":5,\"choiceName\":\"매우 긍정적이다.\"}]}," +
                "{\"title\":\"이 질문에 대한 답변을 생각해보지 않고 무작위로 선택했습니다.\",\"correct_answer\":\"그렇다.\",\"type\":2,\"choiceList\":[{\"id\":1,\"choiceName\":\"그렇다.\"},{\"id\":2,\"choiceName\":\"그렇지 않다.\"},{\"id\":3,\"choiceName\":\"잘 모르겠다.\"},{\"id\":4,\"choiceName\":\"그렇다고 말할 수 있다.\"}]}," +
                "{\"title\":\"이 설문에 진심으로 참여하고 있나요?\",\"correct_answer\":\"매우 그렇다.\",\"type\":2,\"choiceList\":[{\"id\":1,\"choiceName\":\"전혀 아니다.\"},{\"id\":2,\"choiceName\":\"아니다.\"},{\"id\":3,\"choiceName\":\"잘 모르겠다.\"},{\"id\":4,\"choiceName\":\"어느 정도 아니다.\"},{\"id\":5,\"choiceName\":\"매우 그렇다.\"}]}," +
                "{\"title\":\"메뚜기의 종류를 3000개이상 알고 있다\",\"correct_answer\":\"아니다.\",\"type\":2,\"choiceList\":[{\"id\":1,\"choiceName\":\"아니다.\"},{\"id\":2,\"choiceName\":\"그렇다.\"}]}," +
                "{\"title\":\"설문조사의 목적과 내용을 이해하고 진정성을 유지하며 응답하고 있습니까?\",\"correct_answer\":\"매우 그렇다.\",\"type\":2,\"choiceList\":[{\"id\":1,\"choiceName\":\"전혀 아니다.\"},{\"id\":2,\"choiceName\":\"아니다.\"},{\"id\":3,\"choiceName\":\"잘 모르겠다.\"},{\"id\":4,\"choiceName\":\"어느 정도 아니다.\"},{\"id\":5,\"choiceName\":\"매우 그렇다.\"}]}," +
                "{\"title\":\"이 설문조사에 참여하는 데 얼마나 진지하게 접근하고 있나요?\",\"correct_answer\":\"매우 진지하게 접근하고 있다.\",\"type\":2,\"choiceList\":[{\"id\":1,\"choiceName\":\"매우 진지하게 접근하고 있다.\"},{\"id\":2,\"choiceName\":\"부주의하게 접근하고 있다.\"},{\"id\":3,\"choiceName\":\"아주 부주의하게 접근하고 있다..\"}]}" +
                "]}";

        ObjectMapper objectMapper = new ObjectMapper();
        ReliabilityQuestionRequest questionRequest = objectMapper.readValue(jsonString, ReliabilityQuestionRequest.class);

        reliabilityquestionNumber=random.nextInt(10);

        // Access the converted Java object
        questions = questionRequest.getQuestionRequest();
        ReliabilityQuestion question1=questions.get(reliabilityquestionNumber);
        List<ReliabilityChoice> Rchoices = question1.getChoiceList();
        return question1;

//        for (ReliabilityQuestion question : questions) {
//            System.out.println("Title: " + question.getTitle());
//            System.out.println("Type: " + question.getType());
//            System.out.println("Correct Answer: " + question.getCorrect_answer());
//            List<ReliabilityChoice> Rchoices = question.getChoiceList();
//            for (ReliabilityChoice choice : Rchoices) {
//                System.out.println("Choice ID: " + choice.getId());
//                System.out.println("Choice Name: " + choice.getChoiceName());
//            }
//        }
    }
    @Transactional
    public void createSurvey(HttpServletRequest request, SurveyRequestDto surveyRequest) throws InvalidTokenException, UnknownHostException {

        // 유저 정보 받아오기
//        checkInvalidToken(request);
        log.info("유저 정보 받아옴");

        // 유저 정보에 해당하는 Survey 저장소 가져오기
        log.info(String.valueOf(request));
        Survey userSurvey = userService.getUser(request).getSurvey();
        if(userSurvey == null) {
            userSurvey = Survey.builder()
                    .user(userService.getUser(request))
                    .surveyDocumentList(new ArrayList<>())
                    .build();
            surveyRepository.save(userSurvey);
        }

        // Survey Request 를 Survey Document 에 저장하기
        SurveyDocument surveyDocument = SurveyDocument.builder()
                .survey(userSurvey)
                .title(surveyRequest.getTitle())
                .description(surveyRequest.getDescription())
                .type(surveyRequest.getType())
                .questionDocumentList(new ArrayList<>())
                .reliability(surveyRequest.getReliability())
                .font(surveyRequest.getFont())
                .size(surveyRequest.getSize())
                .backcolor(surveyRequest.getBackcolor())
                .surveyAnswerList(new ArrayList<>())
                .countAnswer(0)
                .build();
        surveyDocumentRepository.save(surveyDocument);

        // 설문 문항
        surveyDocumentRepository.findById(surveyDocument.getId());
        for (QuestionRequestDto questionRequestDto : surveyRequest.getQuestionRequest()) {
            // 설문 문항 저장
            QuestionDocument questionDocument = QuestionDocument.builder()
                    .surveyDocument(surveyDocumentRepository.findById(surveyDocument.getId()).get())
                    .title(questionRequestDto.getTitle())
                    .questionType(questionRequestDto.getType())
                    .build();
            questionDocumentRepository.save(questionDocument);

            if(questionRequestDto.getType() == 0) continue; // 주관식

            // 객관식, 찬부식일 경우 선지 저장
            questionDocument.setChoiceList(new ArrayList<>());
            for(ChoiceRequestDto choiceRequestDto : questionRequestDto.getChoiceList()) {
                Choice choice = Choice.builder()
                        .question_id(questionDocumentRepository.findById(questionDocument.getId()).get())
                        .title(choiceRequestDto.getChoiceName())
                        .count(0)
                        .build();
                choiceRepository.save(choice);
                questionDocument.setChoice(choice);
            }
            surveyDocument.setQuestion(questionDocument);
            // choice 가 추가될 때마다 변경되는 Question Document 정보 저장
            questionDocumentRepository.flush();
        }
        // question 이 추가될 때마다 변경되는 Survey Document 정보 저장
        surveyDocumentRepository.flush();

        // Survey 에 SurveyDocument 저장
        userSurvey.setDocument(surveyDocument);
        surveyRepository.flush();

//        // 스냅샷 이미지 저장하기
//        // 172.16.210.25 : Image DB VM 접속하기
//        InetAddress imageVM = Inet4Address.getByAddress(new byte[]{(byte) 172, 16, (byte) 210, 25});
//
//        // 스냅샷 찍기
//        GrapzIt
    }

    public void captureSnapshot() {

    }

    // gird method 로 SurveyDocument 조회
    public List<SurveyDocument> readSurveyListByGrid(HttpServletRequest request, PageRequestDto pageRequest) {

        User user = userService.getUser(request);

        return surveyRepository.getSurveyDocumentListGrid(user, pageRequest);
    }

    // list method 로 SurveyDocument 조회
    public Page<SurveyDocument> readSurveyList(HttpServletRequest request, PageRequestDto pageRequest) throws Exception {

        checkInvalidToken(request);

        User user = userService.getUser(request);
        // gird 일 경우 그냥 다 보여주기
//        if(pageRequest.getMethod().equals("grid")) {
//            return surveyRepository.getSurveyDocumentListGrid();
//        }

        PageRequest page = PageRequest.builder()
                .page(pageRequest.getPage())
                .method(pageRequest.getMethod())
                .sortProperties(pageRequest.getSort1()) // date or title
                .direct(pageRequest.getSort2()) // ascending or descending
                .build();

        // Request Method
        // 1. view Method : grid or list
        // 2. what page number
        // 3. sort on What : date or title
        // 4. sort on How : ascending or descending
        Pageable pageable = page.of(page.getSortProperties(), page.getDirection(page.getDirect()));

        return surveyRepository.surveyDocumentPaging(user, pageable);
    }

    public SurveyDetailDto readSurveyDetail(HttpServletRequest request, Long id) throws InvalidTokenException {

        checkInvalidToken(request);
//        User user = userService.getUser(request);
//
//        surveyRepository.findByUser(user.getId())
//                .getSurveyDocumentList().get()
        return getSurveyDetailDto(id);
    }

    public SurveyDocument getSurveyDocument(Long surveyDocumentId) {
        return surveyDocumentRepository.findById(surveyDocumentId).get();
    }

    //count +1
    //응답자 수 +1
    public void countChoice(Long choiceId) {
        Optional<Choice> findChoice = choiceRepository.findById(choiceId);

        if (findChoice.isPresent()) {
            //todo: querydsl로 변경
            findChoice.get().setCount(findChoice.get().getCount() + 1);
            choiceRepository.flush();
        }
    }

    // 회원 유효성 검사, token 존재하지 않으면 예외처리
    private static void checkInvalidToken(HttpServletRequest request) throws InvalidTokenException {
        if(request.getHeader("Authorization") == null) {
            log.info("error");
            throw new InvalidTokenException();
        }
        log.info("토큰 체크 완료");
    }

    // SurveyDocument Response 보낼 SurveyDetailDto로 변환하는 메서드
    private SurveyDetailDto getSurveyDetailDto(Long surveyDocumentId) {
        SurveyDocument surveyDocument = surveyDocumentRepository.findById(surveyDocumentId).get();
        SurveyDetailDto surveyDetailDto = new SurveyDetailDto();
        ReliabilityQuestion reliabilityQuestion = null;
        QuestionDetailDto reliabilityQuestionDto = new QuestionDetailDto();
        List<ChoiceDetailDto> reliabiltyChoiceDtos = new ArrayList<>();
        if(surveyDocument.getReliability()){
            try {
                Long l = Long.valueOf(-1);
                reliabilityQuestion = reliabilityQuestion();
                reliabilityQuestionDto.setId(l);
                reliabilityQuestionDto.setTitle(reliabilityQuestion.getTitle());
                reliabilityQuestionDto.setQuestionType(reliabilityQuestion.getType());
                List<ReliabilityChoice> Rchoices = reliabilityQuestion.getChoiceList();
                for (ReliabilityChoice choice : Rchoices) {
                    ChoiceDetailDto choiceDto = new ChoiceDetailDto();
                    choiceDto.setId(l);
                    choiceDto.setTitle(choice.getChoiceName());
                    choiceDto.setCount(0);
                    reliabiltyChoiceDtos.add(choiceDto);
                }
                reliabilityQuestionDto.setChoiceList(reliabiltyChoiceDtos);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
        // SurveyDocument에서 SurveyParticipateDto로 데이터 복사
        surveyDetailDto.setId(surveyDocument.getId());
        surveyDetailDto.setTitle(surveyDocument.getTitle());
        surveyDetailDto.setDescription(surveyDocument.getDescription());

        surveyDetailDto.setFont(surveyDocument.getFont());
        surveyDetailDto.setSize(surveyDocument.getSize());
        surveyDetailDto.setBackcolor(surveyDocument.getBackcolor());
        surveyDetailDto.setReliability(surveyDocument.getReliability());
        List<QuestionDetailDto> questionDtos = new ArrayList<>();
        for (QuestionDocument questionDocument : surveyDocument.getQuestionDocumentList()) {
            QuestionDetailDto questionDto = new QuestionDetailDto();
            questionDto.setId(questionDocument.getId());
            questionDto.setTitle(questionDocument.getTitle());
            questionDto.setQuestionType(questionDocument.getQuestionType());

            // question type에 따라 choice 에 들어갈 내용 구분
            // 주관식이면 choice title에 주관식 응답을 저장??
            // 객관식 찬부식 -> 기존 방식 과 똑같이 count를 올려서 저장
            List<ChoiceDetailDto> choiceDtos = new ArrayList<>();
            if (questionDocument.getQuestionType() == 0) {
                // 주관식 답변들 리스트
//                List<QuestionAnswer> questionAnswersByCheckAnswerId = questionAnswerRepository.findQuestionAnswersByCheckAnswerId(questionDocument.getId());
                //REST API GET questionAnswersByCheckAnswerId
                List<QuestionAnswer> questionAnswersByCheckAnswerId = getQuestionAnswersByCheckAnswerId(questionDocument.getId());
                for (QuestionAnswer questionAnswer : questionAnswersByCheckAnswerId) {
                    // 그 중에 주관식 답변만
                    if (questionAnswer.getQuestionType() == 0) {
                        ChoiceDetailDto choiceDto = new ChoiceDetailDto();
                        choiceDto.setId(questionAnswer.getId());
                        choiceDto.setTitle(questionAnswer.getCheckAnswer());
                        choiceDto.setCount(0);

                        choiceDtos.add(choiceDto);
                    }
                }
            } else {
                for (Choice choice : questionDocument.getChoiceList()) {
                    ChoiceDetailDto choiceDto = new ChoiceDetailDto();
                    choiceDto.setId(choice.getId());
                    choiceDto.setTitle(choice.getTitle());
                    choiceDto.setCount(choice.getCount());

                    choiceDtos.add(choiceDto);
                }
            }
            questionDto.setChoiceList(choiceDtos);

            List<WordCloudDto> wordCloudDtos = new ArrayList<>();
            for (WordCloud wordCloud : questionDocument.getWordCloudList()) {
                WordCloudDto wordCloudDto = new WordCloudDto();
                wordCloudDto.setId(wordCloud.getId());
                wordCloudDto.setTitle(wordCloud.getTitle());
                wordCloudDto.setCount(wordCloud.getCount());

                wordCloudDtos.add(wordCloudDto);
            }
            questionDto.setWordCloudDtos(wordCloudDtos);

            questionDtos.add(questionDto);
        }
        if(surveyDocument.getReliability()) {
            reliabilityquestionNumber = random.nextInt(questionDtos.size());
            questionDtos.add(reliabilityquestionNumber, reliabilityQuestionDto);
        }
        surveyDetailDto.setQuestionList(questionDtos);

        log.info(String.valueOf(surveyDetailDto));
        return surveyDetailDto;
    }

    private List<QuestionAnswer> getQuestionAnswersByCheckAnswerId(Long id) {
        //REST API로 분석 시작 컨트롤러로 전달
        // Create a WebClient instance
        log.info("GET questionAnswer List by checkAnswerId");
        WebClient webClient = WebClient.create();

        // Define the API URL
        String apiUrl = "http://"+ gateway +"/survey/internal/getQuestionAnswerByCheckAnswerId/"+ id;

        // Make a GET request to the API and retrieve the response
        List<QuestionAnswer> questionAnswerList = webClient.get()
                .uri(apiUrl)
                .header("Authorization", "NotNull")
                .retrieve()
                .bodyToMono(String.class)
                .map(responseBody -> {
                    ObjectMapper mapper = new ObjectMapper();
                    try {
                        return mapper.readValue(responseBody, new TypeReference<List<QuestionAnswer>>() {});
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .blockOptional()
                .orElse(null);

        // Process the response as needed
        System.out.println("Request: " + questionAnswerList);

        return questionAnswerList;
    }

    public Choice getChoice(Long id) {
        Optional<Choice> byId = choiceRepository.findById(id);
        return byId.get();
    }

    public QuestionDocument getQuestion(Long id) {
        Optional<QuestionDocument> byId = questionDocumentRepository.findById(id);
        return byId.get();
    }

    public QuestionDocument getQuestionByChoiceId(Long id) {
        return choiceRepository.findById(id).get().getQuestion_id();
    }

    public void setWordCloud(Long id, List<WordCloudDto> wordCloudDtos) {
        List<WordCloud> wordCloudList = new ArrayList<>();
        for (WordCloudDto wordCloudDto : wordCloudDtos) {
            WordCloud wordCloud = new WordCloud();
            wordCloud.setId(wordCloudDto.getId());
            wordCloud.setTitle(wordCloudDto.getTitle());
            wordCloud.setCount(wordCloudDto.getCount());
            wordCloud.setQuestionDocument(questionDocumentRepository.findById(id).get());
            wordCloudList.add(wordCloud);
        }
        wordCloudRepository.deleteAllByQuestionDocument(questionDocumentRepository.findById(id).get());
        questionDocumentRepository.findById(id).get().setWordCloudList(wordCloudList);
        questionDocumentRepository.flush();
    }

    public void countAnswer(Long id) {
        Optional<SurveyDocument> byId = surveyDocumentRepository.findById(id);
        byId.get().setCountAnswer(byId.get().getCountAnswer() + 1);
        surveyDocumentRepository.flush();
    }
}
