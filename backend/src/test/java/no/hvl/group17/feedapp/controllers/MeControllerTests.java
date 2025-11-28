package no.hvl.group17.feedapp.controllers;

import no.hvl.group17.feedapp.domain.Option;
import no.hvl.group17.feedapp.domain.Poll;
import no.hvl.group17.feedapp.domain.User;
import no.hvl.group17.feedapp.repositories.PollRepo;
import no.hvl.group17.feedapp.repositories.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Disabled("Temporarily disabled until AUTH is ready")
// todo fix tests
public class MeControllerTests {
    // todo

    // getMe_asValidUser_ok
    // getMe_asOtherUser_forbidden
    // getMe_asOtherUserAdmin_forbidden

    // getMyPolls_asValidUser_ok
    // getMyPolls_asOtherUser_forbidden
    // getMyPolls_asOtherUserAdmin_forbidden

    // getMyVotes_asValidUser_ok
    // getMyVotes_asOtherUser_forbidden
    // getMyVotes_asOtherUserAdmin_forbidden
}
