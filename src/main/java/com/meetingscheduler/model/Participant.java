package com.meetingscheduler.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "participants")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Meeting meeting;

    @ManyToOne(optional = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private ParticipantStatus responseStatus;
}
