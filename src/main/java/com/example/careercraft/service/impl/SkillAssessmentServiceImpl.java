package com.example.careercraft.service.impl;

import com.example.careercraft.dto.SkillAssessmentDto;
import com.example.careercraft.entity.Customer;
import com.example.careercraft.entity.Skill;
import com.example.careercraft.entity.SkillAssessment;
import com.example.careercraft.exception.AppException;
import com.example.careercraft.exception.NotFoundException;
import com.example.careercraft.repository.CustomerRepository;
import com.example.careercraft.repository.SkillAssessmentRepository;
import com.example.careercraft.repository.SkillRepository;
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

    @Override
    public void saveAssessment(SkillAssessmentDto assessmentDto) {
        // Проверяем, существует ли уже оценка для данного клиента и навыка
        if (skillAssessmentRepository.existsByCustomerIdAndSkillId(assessmentDto.getCustomerId(), assessmentDto.getSkillId())) {
            throw new AppException(HttpStatus.CONFLICT, "Assessment already exists for customerId: " + assessmentDto.getCustomerId() + " and skillId: " + assessmentDto.getSkillId());
        }

        SkillAssessment assessment = convertToEntity(assessmentDto);
        skillAssessmentRepository.save(assessment);
    }

    @Override
    public SkillAssessmentDto getAssessment(Long customerId, Long skillId) {
        SkillAssessment assessment = skillAssessmentRepository.findByCustomerIdAndSkillId(customerId, skillId)
                .orElseThrow(() -> new NotFoundException("Assessment not found for customerId: " + customerId + " and skillId: " + skillId));
        return convertToDto(assessment);
    }

    private SkillAssessment convertToEntity(SkillAssessmentDto dto) {
        SkillAssessment entity = new SkillAssessment();
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new NotFoundException("Customer not found with id: " + dto.getCustomerId()));
        Skill skill = skillRepository.findById(dto.getSkillId())
                .orElseThrow(() -> new NotFoundException("Skill not found with id: " + dto.getSkillId()));

        entity.setCustomer(customer);
        entity.setSkill(skill);
        entity.setSkillLevel(dto.getSkillLevel());
        return entity;
    }

    private SkillAssessmentDto convertToDto(SkillAssessment entity) {
        SkillAssessmentDto dto = new SkillAssessmentDto();
        dto.setCustomerId(entity.getCustomer().getId());
        dto.setSkillId(entity.getSkill().getId());
        dto.setSkillLevel(entity.getSkillLevel());
        return dto;
    }
}