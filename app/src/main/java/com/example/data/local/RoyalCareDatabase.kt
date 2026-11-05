package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Appointment
import com.example.data.model.AuditLog
import com.example.data.model.Doctor
import com.example.data.model.GatewayConfig
import com.example.data.model.PaymentOrder
import com.example.data.model.RefundRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    Doctor::class,
    PaymentOrder::class,
    Appointment::class,
    GatewayConfig::class,
    AuditLog::class,
    RefundRecord::class
  ],
  version = 1,
  exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RoyalCareDatabase : RoomDatabase() {
  abstract fun doctorDao(): DoctorDao
  abstract fun paymentOrderDao(): PaymentOrderDao
  abstract fun appointmentDao(): AppointmentDao
  abstract fun gatewayConfigDao(): GatewayConfigDao
  abstract fun auditLogDao(): AuditLogDao
  abstract fun refundDao(): RefundDao

  companion object {
    @Volatile
    private var INSTANCE: RoyalCareDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): RoyalCareDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          RoyalCareDatabase::class.java,
          "royal_care_database"
        )
          .addCallback(RoyalCareDatabaseCallback(scope))
          .build()
        INSTANCE = instance
        instance
      }
    }

    private class RoyalCareDatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            populateInitialData(database)
          }
        }
      }
    }

    suspend fun populateInitialData(database: RoyalCareDatabase) {
      val doctorDao = database.doctorDao()
      val gatewayDao = database.gatewayConfigDao()

      // Seed initial gateway config
      gatewayDao.insertConfig(GatewayConfig())

      // Seed renowned specialist doctors
      val initialDoctors = listOf(
        Doctor(
          id = "doc-01",
          name = "Prof. Dr. Farhana Ahmed",
          title = "MBBS, FCPS (Medicine), FRCP (Edin)",
          specialty = "Cardiology & Internal Medicine",
          hospital = "Square Hospitals & Royal Care Specialist Hub",
          bmdcRegNo = "BMDC A-48291",
          experienceYears = 16,
          rating = 4.95f,
          reviewCount = 384,
          isOnline = true,
          availableHours = "09:00 AM - 01:00 PM, 05:00 PM - 09:30 PM",
          videoFee = 1200.0,
          audioFee = 800.0,
          instantFeePremium = 300.0,
          scheduledFee = 1000.0,
          consultationDurationMinutes = 20,
          bio = "Senior Consultant Cardiologist specializing in preventive heart disease, hypertension, cholesterol management, and digital telemedicine consultations."
        ),
        Doctor(
          id = "doc-02",
          name = "Dr. Tanvir Rahman",
          title = "MBBS, MD (Pediatrics), DCH (Glasgow)",
          specialty = "Pediatrics & Child Care",
          hospital = "Dhaka Shishu Hospital & Royal Care Clinic",
          bmdcRegNo = "BMDC A-51042",
          experienceYears = 12,
          rating = 4.90f,
          reviewCount = 290,
          isOnline = true,
          availableHours = "10:00 AM - 02:00 PM, 06:00 PM - 10:00 PM",
          videoFee = 1000.0,
          audioFee = 700.0,
          instantFeePremium = 250.0,
          scheduledFee = 900.0,
          consultationDurationMinutes = 15,
          bio = "Compassionate child healthcare specialist. Expert in neonatal nutrition, pediatric infections, allergy management, and adolescent medicine."
        ),
        Doctor(
          id = "doc-03",
          name = "Dr. Nusrat Jahan",
          title = "MBBS, FCPS (Gynecology & Obstetrics), MS",
          specialty = "Gynecology & Women's Health",
          hospital = "United Hospital & Royal Care Teleclinic",
          bmdcRegNo = "BMDC A-43920",
          experienceYears = 14,
          rating = 4.92f,
          reviewCount = 415,
          isOnline = false,
          availableHours = "04:00 PM - 08:30 PM",
          videoFee = 1500.0,
          audioFee = 1000.0,
          instantFeePremium = 400.0,
          scheduledFee = 1300.0,
          consultationDurationMinutes = 25,
          bio = "High-risk pregnancy consultant, maternal reproductive health expert, and hormonal care advocate with over 14 years of clinical excellence."
        ),
        Doctor(
          id = "doc-04",
          name = "Dr. Kazi Mahfuzur Rahman",
          title = "MBBS, DDV, MD (Dermatology)",
          specialty = "Dermatology & Skin Specialist",
          hospital = "Apollo Royal Aesthetics & Telecare",
          bmdcRegNo = "BMDC A-62819",
          experienceYears = 10,
          rating = 4.88f,
          reviewCount = 210,
          isOnline = true,
          availableHours = "11:00 AM - 03:00 PM, 07:00 PM - 11:00 PM",
          videoFee = 1100.0,
          audioFee = 750.0,
          instantFeePremium = 200.0,
          scheduledFee = 950.0,
          consultationDurationMinutes = 15,
          bio = "Clinical dermatologist focused on acne management, eczema, cosmetic dermatology, and high-definition telemedicine tele-dermatology."
        ),
        Doctor(
          id = "doc-05",
          name = "Dr. Syed Anisul Haque",
          title = "MBBS, FCPS (Psychiatry), MACP",
          specialty = "Psychiatry & Mental Wellness",
          hospital = "National Institute of Mental Health & Royal Care",
          bmdcRegNo = "BMDC A-39871",
          experienceYears = 18,
          rating = 4.97f,
          reviewCount = 520,
          isOnline = false,
          availableHours = "06:00 PM - 10:00 PM",
          videoFee = 1800.0,
          audioFee = 1200.0,
          instantFeePremium = 500.0,
          scheduledFee = 1600.0,
          consultationDurationMinutes = 30,
          bio = "Confidential, empathetic mental health counseling for stress, anxiety, depression, sleep disorders, and psychotherapeutic telemedicine."
        )
      )

      doctorDao.insertDoctors(initialDoctors)
    }
  }
}
