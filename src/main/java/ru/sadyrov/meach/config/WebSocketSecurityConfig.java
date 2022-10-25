//package ru.sadyrov.meach.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.simp.config.ChannelRegistration;
//import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
//import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import ru.sadyrov.meach.security.JwtFilter;
//
//@Configuration
//public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {
//    @Override
//    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
//        messages
//                .nullDestMatcher().authenticated()
//                .simpDestMatchers("/app/**").authenticated()
//                .anyMessage().authenticated()
//                .simpSubscribeDestMatchers("/topic/**").authenticated();
//    }
//}
