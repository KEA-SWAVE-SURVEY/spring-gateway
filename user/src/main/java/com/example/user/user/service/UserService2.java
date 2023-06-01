package com.example.user.user.service;

import com.example.user.survey.domain.Survey;
import com.example.user.survey.domain.SurveyDocument;
import com.example.user.survey.response.SurveyMyPageDto;
import com.example.user.user.domain.User;
import com.example.user.user.exception.UserNotFoundException;
import com.example.user.user.repository.UserRepository;
import com.example.user.user.request.UserUpdateRequest;
import com.example.user.util.OAuth.JwtProperties;
import com.example.user.util.OAuth.OauthToken;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService2 {
    @Autowired
    OAuthService oAuthService;
    @Autowired
    UserRepository userRepository;

    public List<SurveyMyPageDto> mySurveyList(HttpServletRequest request) {
        List<SurveyMyPageDto> surveyMyPageDtos = new ArrayList<>();

        Survey survey= getUser(request).getSurvey();
        System.out.println(survey);
        for(SurveyDocument surveyDocument:survey.getSurveyDocumentList()){
            SurveyMyPageDto surveyMyPageDto = new SurveyMyPageDto();
            surveyMyPageDto.setId(surveyDocument.getId());
            surveyMyPageDto.setDescription(surveyDocument.getDescription());
            surveyMyPageDto.setTitle(surveyDocument.getTitle());
            surveyMyPageDto.setDeadline(surveyDocument.getDeadline());
            surveyMyPageDto.setStartDate(surveyDocument.getStartDate());
            surveyMyPageDtos.add(surveyMyPageDto);

        }
        return surveyMyPageDtos;
    }

    public User getUser(HttpServletRequest request) { //(1)
        Long userCode = (Long) request.getAttribute("userCode");
        User user = userRepository.findByUserCode(userCode).orElseThrow(UserNotFoundException::new);
        return user;
    }

    public ResponseEntity<Object> getCurrentUser(HttpServletRequest request) throws Exception {
        User user = getUser(request);
        System.out.println(user.getEmail());
        return ResponseEntity.ok().body(user);
    }

    public ResponseEntity getLogin(String code,String provider){
        OauthToken oauthToken = oAuthService.getAccessToken(code, provider);
        String jwtToken = oAuthService.SaveUserAndGetToken(oauthToken.getAccess_token(), provider);
        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);
        return ResponseEntity.ok().headers(headers).body("\"success\"");
    }
    public String updateMyPage(HttpServletRequest request, UserUpdateRequest userUpdateRequest) throws ServletException {
        Long userId =getUser(request).getUserCode();
        Optional<User> optionalUser = userRepository.findByUserCode(userId);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            user.setNickname(userUpdateRequest.getNickname());
            user.setDescription(userUpdateRequest.getDescription());
            userRepository.save(user);
        }else {
            throw new ServletException();
        }
        return "success";
    }

//    private static void checkInvalidToken(HttpServletRequest request) throws InvalidTokenException {
//        if(request.getHeader("Authorization") == null) {
//            log.info("error");
//            throw new InvalidTokenException();
//        }
//        log.info("토큰 체크 완료");
//    }


}
