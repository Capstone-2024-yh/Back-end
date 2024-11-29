//package com.capstone.backend.Entity
//
//import jakarta.persistence.*
//
//@Entity
//@Table(name = "message")
//data class Message (
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    val id: Int = 0,
//
//    @Column(name = "feedback", nullable = true, columnDefinition = "text[]")
//    @ElementCollection
//    val feedback : List<Int>,
//
//    @Column(name = "venueidlist", nullable = true, columnDefinition = "integer[]")
//    @ElementCollection
//    val venueIdList : List<Int>
//)