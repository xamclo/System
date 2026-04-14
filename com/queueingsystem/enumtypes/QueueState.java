package com.queueingsystem.enumtypes;

//  Enum for status ng ticket sa queue
// Used para malaman kung anong state na yung student sa pila
public enum QueueState {

    // Waiting pa sa pila (hindi pa natawag)
    WAITING,

    // Currently sineserve na sa window
    SERVING,

    // Tapos na yung transaction
    COMPLETED,

    //   (hindi present or nilagpasan)
    SKIPPED
}