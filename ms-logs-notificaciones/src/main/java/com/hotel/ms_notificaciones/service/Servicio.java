package com.hotel.ms_notificaciones.service;

import com.hotel.ms_notificaciones.dto.NotificacionRequest;
import com.hotel.ms_notificaciones.entity.NotificacionEntity;
import com.hotel.ms_notificaciones.exception.NotificacionNotFoundException;
import com.hotel.ms_notificaciones.repositorio.NotificacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class Servicio {

        private final NotificacionRepository repository;
        private final JavaMailSender mailSender;


        public void procesarNotificacion(NotificacionRequest request) {
            validarNotificacion(request);
            NotificacionEntity entity = NotificacionEntity.builder()
                    .destinatario(request.getDestinatario())
                    .asunto(request.getAsunto())
                    .cuerpo(request.getCuerpo())
                    .estado("PENDIENTE")
                    .build();

            repository.save(entity);
            log.info("Notificación guardada en BD para: {}", request.getDestinatario());

            intentarEnviar(entity);
        }

    private void validarNotificacion(NotificacionRequest request) {
        if (request.getDestinatario() == null || request.getDestinatario().isBlank()) {
            throw new RuntimeException("Error: El destinatario no puede ser nulo o vacío");
        }
        }

        private void intentarEnviar(NotificacionEntity entity) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(entity.getDestinatario());
                message.setSubject(entity.getAsunto());
                message.setText(entity.getCuerpo());

                mailSender.send(message);

                entity.setEstado("ENVIADO");
                repository.save(entity);
                log.info("Correo enviado exitosamente a: {}", entity.getDestinatario());
            } catch (Exception e) {
                log.error("Fallo técnico al enviar correo a {}: {}", entity.getDestinatario(), e.getMessage());
                entity.setIntentos(entity.getIntentos() + 1);
                repository.save(entity);
            }
        }

        @Scheduled(fixedRate = 300000)
        public void reintentarEnviosPendientes() {
            List<NotificacionEntity> pendientes = repository.findByEstado("PENDIENTE");
            if (!pendientes.isEmpty()) {
                log.info("Encontradas {} notificaciones pendientes, iniciando reintento...", pendientes.size());
                pendientes.forEach(this::intentarEnviar);
            }
        }

    }
