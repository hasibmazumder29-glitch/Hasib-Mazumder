package com.example.data.model

enum class ConsultationType(val label: String) {
  VIDEO("Video Consultation"),
  AUDIO("Audio Consultation")
}

enum class BookingType(val label: String) {
  INSTANT("Instant Doctor Call"),
  SCHEDULED("Scheduled Consultation")
}

enum class PaymentStatus(val label: String) {
  INITIATED("Initiated"),
  PENDING("Pending Verification"),
  SUCCESSFUL("Successful (Paid)"),
  FAILED("Failed"),
  REFUNDED("Refunded"),
  PARTIALLY_REFUNDED("Partially Refunded")
}

enum class PaymentMethod(val displayName: String, val channel: String) {
  BKASH("bKash", "Mobile Financial Service"),
  NAGAD("Nagad", "Post Office Digital Service"),
  ROCKET("Rocket", "DBBL Mobile Banking"),
  UPAY("Upay", "UCB Fintech"),
  CARD("Visa / Mastercard / Amex", "Card & Internet Banking")
}

enum class AppointmentStatus(val label: String) {
  SCHEDULED("Scheduled"),
  READY_TO_JOIN("Ready to Join"),
  IN_CALL("Call in Progress"),
  COMPLETED("Completed"),
  CANCELLED("Cancelled")
}

enum class GatewayProvider(val displayName: String) {
  SANDBOX_SIMULATOR("Royal Care Official Sandbox (Simulated APIs)"),
  BKASH_CHECKOUT("bKash Merchant Tokenized Checkout API"),
  NAGAD_DIRECT("Nagad Merchant Direct Gateway API"),
  SSLCOMMERZ("SSLCommerz Payment Gateway"),
  SHURJOPAY("ShurjoPay Merchant Gateway"),
  AAMARPAY("AamarPay Merchant Gateway")
}
