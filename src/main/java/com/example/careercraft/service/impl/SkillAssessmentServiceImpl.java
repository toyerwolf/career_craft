package com.example.careercraft.service.impl;

import com.example.careercraft.dto.CustomerInfo;
import com.example.careercraft.dto.SkillAssessmentDto;
import com.example.careercraft.entity.Customer;
import com.example.careercraft.entity.Skill;
import com.example.careercraft.entity.SkillAssessment;
import com.example.careercraft.exception.AppException;
import com.example.careercraft.exception.NotFoundException;
import com.example.careercraft.repository.CustomerRepository;
import com.example.careercraft.repository.SkillAssessmentRepository;
import com.example.careercraft.repository.SkillRepository;
import com.example.careercraft.req.SkillAssessmentRequest;
import com.example.careercraft.service.AuthService;
import com.example.careercraft.service.SkillAssessmentService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SkillAssessmentServiceImpl implements SkillAssessmentService {


    private final SkillAssessmentRepository skillAssessmentRepository;
    private final CustomerRepository customerRepository;
    private final SkillRepository skillRepository;
    private final AuthService authService;

    @Override
    public SkillAssessmentDto saveAssessment(String authHeader, SkillAssessmentRequest skillAssessmentRequest) {
        // Получаем customerId из токена
        CustomerInfo customerInfo = authService.getCustomerDetailsFromToken(authHeader);
        Long customerId = customerInfo.getId();

        // Проверяем, существует ли уже оценка для данного клиента и навыка
        if (skillAssessmentRepository.existsByCustomerIdAndSkillId(customerId, skillAssessmentRequest.getSkillId())) {
            throw new AppException(HttpStatus.CONFLICT, "Assessment already exists for customerId: " + customerId + " and skillId: " + skillAssessmentRequest.getSkillId());
        }

        // Конвертируем запрос в сущность и сохраняем
        SkillAssessment assessment = convertToEntity(skillAssessmentRequest, customerId);
        SkillAssessment savedAssessment = skillAssessmentRepository.save(assessment);

        // Возвращаем DTO сохраненной сущности
        return convertToDto(savedAssessment);
    }

    @Override
    public SkillAssessmentDto getAssessment(String authHeader, Long skillId) {
        // Получаем customerId из токена
        CustomerInfo customerInfo = authService.getCustomerDetailsFromToken(authHeader);
        Long customerId = customerInfo.getId();

        // Находим оценку для клиента и навыка
        SkillAssessment assessment = skillAssessmentRepository.findByCustomerIdAndSkillId(customerId, skillId)
                .orElseThrow(() -> new NotFoundException("Assessment not found for customerId: " + customerId + " and skillId: " + skillId));

        // Конвертируем в DTO и возвращаем
        return convertToDto(assessment);
    }
    private SkillAssessment convertToEntity(SkillAssessmentRequest request, Long customerId) {
        SkillAssessment entity = new SkillAssessment();
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found with id: " + customerId));
        Skill skill = skillRepository.findById(request.getSkillId())
                .orElseThrow(() -> new NotFoundException("Skill not found with id: " + request.getSkillId()));

        entity.setCustomer(customer);
        entity.setSkill(skill);
        entity.setSkillLevel(request.getSkillLevel());
        return entity;
    }

    private SkillAssessmentDto convertToDto(SkillAssessment entity) {
        SkillAssessmentDto dto = new SkillAssessmentDto();
        dto.setSkillId(entity.getSkill().getId());
        dto.setSkillLevel(entity.getSkillLevel());
        return dto;
    }
}