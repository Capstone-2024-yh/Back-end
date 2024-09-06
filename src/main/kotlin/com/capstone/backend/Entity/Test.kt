package com.capstone.backend.Entity

import jakarta.persistence.*

@Entity
@Table(name = "example_table") // 테이블 이름 명시
data class ExampleTable(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int = 0,

    val text: String
)