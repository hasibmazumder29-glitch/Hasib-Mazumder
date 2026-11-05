package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.AppointmentStatus
import com.example.data.model.BookingType
import com.example.data.model.ConsultationType
import com.example.data.model.GatewayProvider
import com.example.data.model.PaymentMethod
import com.example.data.model.PaymentStatus

class Converters {
  @TypeConverter
  fun fromConsultationType(value: ConsultationType): String = value.name

  @TypeConverter
  fun toConsultationType(value: String): ConsultationType =
    runCatching { ConsultationType.valueOf(value) }.getOrDefault(ConsultationType.VIDEO)

  @TypeConverter
  fun fromBookingType(value: BookingType): String = value.name

  @TypeConverter
  fun toBookingType(value: String): BookingType =
    runCatching { BookingType.valueOf(value) }.getOrDefault(BookingType.SCHEDULED)

  @TypeConverter
  fun fromPaymentStatus(value: PaymentStatus): String = value.name

  @TypeConverter
  fun toPaymentStatus(value: String): PaymentStatus =
    runCatching { PaymentStatus.valueOf(value) }.getOrDefault(PaymentStatus.INITIATED)

  @TypeConverter
  fun fromPaymentMethod(value: PaymentMethod?): String? = value?.name

  @TypeConverter
  fun toPaymentMethod(value: String?): PaymentMethod? =
    value?.let { runCatching { PaymentMethod.valueOf(it) }.getOrNull() }

  @TypeConverter
  fun fromAppointmentStatus(value: AppointmentStatus): String = value.name

  @TypeConverter
  fun toAppointmentStatus(value: String): AppointmentStatus =
    runCatching { AppointmentStatus.valueOf(value) }.getOrDefault(AppointmentStatus.SCHEDULED)

  @TypeConverter
  fun fromGatewayProvider(value: GatewayProvider): String = value.name

  @TypeConverter
  fun toGatewayProvider(value: String): GatewayProvider =
    runCatching { GatewayProvider.valueOf(value) }.getOrDefault(GatewayProvider.SANDBOX_SIMULATOR)
}
