//package com.os.fasdoor.model;
//
//import com.fasterxml.jackson.annotation.JsonBackReference;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.util.Date;
//
//@AllArgsConstructor
//@NoArgsConstructor
//@Setter
//@Getter
//@Entity
//@Table(name = "child_service")
//public class ChildService {
//    @Id
//    private Long id;
//    private String name;
//    @Column(nullable = false)
//    private String createdBy;
//    @Column(nullable = false)
//    private Date createdOn;
//    @ManyToOne
//    @JsonBackReference
//    @JoinColumn(name = "parent_service_id", referencedColumnName = "id", updatable = true, insertable = true)
//    private ParentServices parentService;
//}