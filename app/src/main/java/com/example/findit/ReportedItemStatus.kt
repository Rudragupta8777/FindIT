package com.example.findit

enum class ReportedItemStatus {
    CLAIMED,      // Item has been claimed by someone
    UNCLAIMED,    // Item is unclaimed (show QR)
    DELETED       // Item has been deleted or removed
}