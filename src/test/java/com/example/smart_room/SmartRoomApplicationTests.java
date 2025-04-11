package com.example.smart_room;

import com.example.smart_room.config.FirebaseConfig;
import com.example.smart_room.service.AdafruitMqttSubscriberService;
import com.example.smart_room.service.AdafruitService;
import com.example.smart_room.service.FirebaseService;
import com.example.smart_room.service.OAuth2LoginSuccessHandler;
import com.google.firebase.database.FirebaseDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SmartRoomApplicationTests {

	@MockBean
	private AdafruitMqttSubscriberService adafruitMqttSubscriberService;

	@MockBean
	private AdafruitService adafruitService;

	@MockBean
	private FirebaseConfig firebaseConfig;

	@MockBean
	private FirebaseDatabase firebaseDatabase;

	@MockBean
	private FirebaseService firebaseService;

	@MockBean
	private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
	@Test
	void contextLoads() {
	}

}
