# Smart Care Services - Vue 3 Web Integration Instructions

## 📋 **Table of Contents**
1. [Project Overview](#project-overview)
2. [Prerequisites](#prerequisites)
3. [Vue 3 Project Setup](#vue-3-project-setup)
4. [API Integration Guide](#api-integration-guide)
5. [Service Integrations](#service-integrations)
6. [Authentication & Security](#authentication--security)
7. [Error Handling](#error-handling)
8. [Testing](#testing)
9. [Deployment](#deployment)
10. [Troubleshooting](#troubleshooting)

---

## 🎯 **Project Overview**

Smart Care Services is a comprehensive healthcare management system built with Spring Boot 3.2.0 and Java 17. This document provides detailed instructions for creating a Vue 3 frontend application that integrates with all backend services.

### **Backend Services Available:**
- **Authentication Service** - User registration, login, JWT tokens
- **User Profile Service** - Profile management, health profiles
- **Doctor Service** - Doctor search, management, profiles
- **Appointment Service** - Booking, scheduling, management
- **Medication Service** - Tracking, reminders, prescriptions
- **Health Tracking Service** - Goals, monitoring, analytics

### **Backend Configuration:**
- **Base URL**: `http://localhost:8080`
- **API Context**: `/api/v1`
- **Full Base URL**: `http://localhost:8080/api/v1`
- **Documentation**: `http://localhost:8080/swagger-ui.html`
- **Database Console**: `http://localhost:8080/h2-console`

---

## 🛠️ **Prerequisites**

### **System Requirements:**
- **Node.js**: 18.x or higher
- **npm**: 9.x or higher (or yarn/pnpm)
- **Git**: Latest version
- **Code Editor**: VS Code (recommended)

### **Backend Requirements:**
- **Java**: 17 or higher
- **Smart Care Services**: Running on `http://localhost:8080`

### **Verify Prerequisites:**
```bash
# Check Node.js version
node --version

# Check npm version
npm --version

# Verify backend is running
curl http://localhost:8080/api/v1/actuator/health
```

---

## 🚀 **Vue 3 Project Setup**

### **1. Create Vue 3 Project**
```bash
# Create new Vue 3 project with Vite
npm create vue@latest smartcare-frontend

# Navigate to project directory
cd smartcare-frontend

# Install dependencies
npm install
```

### **2. Project Configuration**
Select the following options when prompted:
- ✅ TypeScript
- ✅ Router
- ✅ Pinia (State Management)
- ✅ ESLint
- ✅ Prettier
- ✅ Vitest (Unit Testing)
- ✅ End-to-End Testing (Playwright)

### **3. Install Additional Dependencies**
```bash
# HTTP Client
npm install axios

# UI Framework (Choose one)
npm install @headlessui/vue @heroicons/vue  # Headless UI
# OR
npm install vuetify  # Vuetify
# OR
npm install @element-plus/vue element-plus  # Element Plus

# Utility Libraries
npm install @vueuse/core  # Vue composition utilities
npm install date-fns  # Date manipulation
npm install lodash-es  # Utility functions
npm install @types/lodash-es  # TypeScript definitions

# Form Validation
npm install @vee-validate/vue @vee-validate/rules yup

# Toast Notifications
npm install vue-toastification

# Loading States
npm install vue-loading-overlay

# Charts (for health analytics)
npm install chart.js vue-chartjs
```

### **4. Project Structure**
```
smartcare-frontend/
├── src/
│   ├── api/                 # API integration files
│   │   ├── auth.ts         # Authentication API
│   │   ├── doctors.ts      # Doctor API
│   │   ├── appointments.ts # Appointment API
│   │   ├── medications.ts  # Medication API
│   │   ├── profile.ts      # Profile API
│   │   ├── health.ts       # Health Tracking API
│   │   └── index.ts        # API configuration
│   ├── components/         # Reusable components
│   │   ├── common/         # Common UI components
│   │   ├── auth/           # Authentication components
│   │   ├── dashboard/      # Dashboard components
│   │   └── forms/          # Form components
│   ├── composables/        # Vue composables
│   ├── stores/             # Pinia stores
│   ├── types/              # TypeScript types
│   ├── utils/              # Utility functions
│   ├── views/              # Page components
│   └── router/             # Vue Router configuration
```

---

## 🔌 **API Integration Guide**

### **1. Create API Configuration**
Create `src/api/index.ts`:
```typescript
import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { useAuthStore } from '@/stores/auth'

// API Configuration
const API_BASE_URL = 'http://localhost:8080/api/v1'
const API_TIMEOUT = 10000

// Create axios instance
const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: API_TIMEOUT,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
})

// Request interceptor for adding auth token
apiClient.interceptors.request.use(
  (config: AxiosRequestConfig) => {
    const authStore = useAuthStore()
    const token = authStore.token
    
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }
    
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor for handling common errors
apiClient.interceptors.response.use(
  (response: AxiosResponse) => {
    return response
  },
  (error) => {
    const authStore = useAuthStore()
    
    // Handle unauthorized access
    if (error.response?.status === 401) {
      authStore.logout()
      // Redirect to login page
      window.location.href = '/login'
    }
    
    // Handle server errors
    if (error.response?.status >= 500) {
      console.error('Server Error:', error.response.data)
    }
    
    return Promise.reject(error)
  }
)

export default apiClient
export { API_BASE_URL }
```

### **2. Create TypeScript Types**
Create `src/types/index.ts`:
```typescript
// API Response Types
export interface ApiResponse<T = any> {
  success: boolean
  message: string
  data?: T
}

// User Types
export interface User {
  id: number
  username: string
  email: string
  firstName: string
  lastName: string
  phoneNumber?: string
  dateOfBirth?: string
  profilePictureUrl?: string
  profileCompleted: boolean
  tourCompleted: boolean
  role: UserRole
  createdAt: string
  updatedAt: string
}

export enum UserRole {
  USER = 'USER',
  DOCTOR = 'DOCTOR',
  ADMIN = 'ADMIN',
  PHARMACY = 'PHARMACY'
}

// Authentication Types
export interface LoginRequest {
  usernameOrEmail: string
  password: string
}

export interface SignUpRequest {
  username: string
  email: string
  password: string
  firstName: string
  lastName: string
  phoneNumber?: string
  dateOfBirth?: string
}

export interface JwtAuthenticationResponse {
  accessToken: string
  tokenType: string
  user: User
}

// Doctor Types
export interface Doctor {
  id: number
  firstName: string
  lastName: string
  email: string
  phoneNumber: string
  specialization: string
  licenseNumber: string
  experience: number
  consultationFee: number
  rating: number
  totalRatings: number
  bio?: string
  education?: string
  certifications?: string
  languages: string[]
  availableFrom: string
  availableTo: string
  workingDays: string[]
  city: string
  state: string
  country: string
  isActive: boolean
  profileImageUrl?: string
  createdAt: string
  updatedAt: string
}

// Appointment Types
export interface Appointment {
  id: number
  patient: User
  doctor: Doctor
  appointmentDateTime: string
  reason: string
  notes?: string
  status: AppointmentStatus
  type: AppointmentType
  meetingLink?: string
  createdAt: string
  updatedAt: string
}

export enum AppointmentStatus {
  SCHEDULED = 'SCHEDULED',
  CONFIRMED = 'CONFIRMED',
  IN_PROGRESS = 'IN_PROGRESS',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED',
  NO_SHOW = 'NO_SHOW'
}

export enum AppointmentType {
  IN_PERSON = 'IN_PERSON',
  VIRTUAL = 'VIRTUAL',
  PHONE = 'PHONE'
}

export interface AppointmentBookingRequest {
  doctorId: number
  appointmentDateTime: string
  reason: string
  type: AppointmentType
}

// Medication Types
export interface Medication {
  id: number
  userId: number
  name: string
  dosage: string
  frequency: string
  startDate: string
  endDate?: string
  remainingQuantity: number
  refillReminder: number
  instructions?: string
  prescriptionInfo?: string
  status: MedicationStatus
  createdAt: string
  updatedAt: string
}

export enum MedicationStatus {
  ACTIVE = 'ACTIVE',
  COMPLETED = 'COMPLETED',
  PAUSED = 'PAUSED',
  CANCELLED = 'CANCELLED'
}

// Health Profile Types
export interface HealthProfile {
  id: number
  userId: number
  height?: number
  weight?: number
  bloodType?: BloodType
  medicalConditions: string[]
  allergies: string[]
  emergencyContactName?: string
  emergencyContactPhone?: string
  additionalNotes?: string
  createdAt: string
  updatedAt: string
}

export enum BloodType {
  A_POSITIVE = 'A_POSITIVE',
  A_NEGATIVE = 'A_NEGATIVE',
  B_POSITIVE = 'B_POSITIVE',
  B_NEGATIVE = 'B_NEGATIVE',
  AB_POSITIVE = 'AB_POSITIVE',
  AB_NEGATIVE = 'AB_NEGATIVE',
  O_POSITIVE = 'O_POSITIVE',
  O_NEGATIVE = 'O_NEGATIVE'
}

// Health Tracking Types
export interface HealthGoal {
  id: number
  userId: number
  title: string
  description?: string
  targetValue: number
  currentValue: number
  unit: string
  status: HealthGoalStatus
  startDate: string
  targetDate: string
  createdAt: string
  updatedAt: string
}

export enum HealthGoalStatus {
  IN_PROGRESS = 'IN_PROGRESS',
  ON_TRACK = 'ON_TRACK',
  COMPLETED = 'COMPLETED'
}

export interface HealthTrackingOverview {
  healthScore: number
  activeGoals: number
  completedGoals: number
  recentActivity: string[]
}
```

---

## 🔐 **Authentication & Security**

### **1. Create Authentication Store**
Create `src/stores/auth.ts`:
```typescript
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User, LoginRequest, SignUpRequest, JwtAuthenticationResponse } from '@/types'
import authApi from '@/api/auth'

export const useAuthStore = defineStore('auth', () => {
  // State
  const user = ref<User | null>(null)
  const token = ref<string | null>(localStorage.getItem('auth_token'))
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  // Getters
  const isAuthenticated = computed(() => !!token.value && !!user.value)
  const userRole = computed(() => user.value?.role)
  const isDoctor = computed(() => userRole.value === 'DOCTOR')
  const isAdmin = computed(() => userRole.value === 'ADMIN')

  // Actions
  const login = async (credentials: LoginRequest): Promise<void> => {
    try {
      isLoading.value = true
      error.value = null

      const response = await authApi.login(credentials)
      const authData: JwtAuthenticationResponse = response.data.data

      // Store token and user data
      token.value = authData.accessToken
      user.value = authData.user
      
      // Persist token
      localStorage.setItem('auth_token', authData.accessToken)
      localStorage.setItem('user_data', JSON.stringify(authData.user))

    } catch (err: any) {
      error.value = err.response?.data?.message || 'Login failed'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const register = async (userData: SignUpRequest): Promise<void> => {
    try {
      isLoading.value = true
      error.value = null

      await authApi.register(userData)
      
    } catch (err: any) {
      error.value = err.response?.data?.message || 'Registration failed'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const logout = (): void => {
    user.value = null
    token.value = null
    localStorage.removeItem('auth_token')
    localStorage.removeItem('user_data')
  }

  const loadUserFromStorage = (): void => {
    const storedToken = localStorage.getItem('auth_token')
    const storedUser = localStorage.getItem('user_data')

    if (storedToken && storedUser) {
      token.value = storedToken
      try {
        user.value = JSON.parse(storedUser)
      } catch {
        logout()
      }
    }
  }

  const clearError = (): void => {
    error.value = null
  }

  return {
    // State
    user,
    token,
    isLoading,
    error,
    // Getters
    isAuthenticated,
    userRole,
    isDoctor,
    isAdmin,
    // Actions
    login,
    register,
    logout,
    loadUserFromStorage,
    clearError
  }
})
```

### **2. Create Authentication API**
Create `src/api/auth.ts`:
```typescript
import apiClient from './index'
import type { LoginRequest, SignUpRequest, ApiResponse, JwtAuthenticationResponse } from '@/types'

const authApi = {
  // Login user
  login: (credentials: LoginRequest) => {
    return apiClient.post<ApiResponse<JwtAuthenticationResponse>>('/auth/signin', credentials)
  },

  // Register new user
  register: (userData: SignUpRequest) => {
    return apiClient.post<ApiResponse>('/auth/signup', userData)
  },

  // Check username availability
  checkUsername: (username: string) => {
    return apiClient.get<ApiResponse>(`/auth/check-username?username=${username}`)
  },

  // Check email availability
  checkEmail: (email: string) => {
    return apiClient.get<ApiResponse>(`/auth/check-email?email=${email}`)
  }
}

export default authApi
```

---

## 📋 **Service Integrations**

### **1. User Profile Service Integration**

Create `src/api/profile.ts`:
```typescript
import apiClient from './index'
import type { User, HealthProfile, ApiResponse } from '@/types'

const profileApi = {
  // Get user profile
  getProfile: () => {
    return apiClient.get<ApiResponse<User>>('/profile')
  },

  // Update user profile
  updateProfile: (userData: Partial<User>) => {
    return apiClient.put<ApiResponse<User>>('/profile', userData)
  },

  // Get health profile
  getHealthProfile: () => {
    return apiClient.get<ApiResponse<HealthProfile>>('/profile/health')
  },

  // Create or update health profile
  updateHealthProfile: (healthData: Partial<HealthProfile>) => {
    return apiClient.post<ApiResponse<HealthProfile>>('/profile/health', healthData)
  },

  // Complete tour
  completeTour: () => {
    return apiClient.post<ApiResponse<User>>('/profile/complete-tour')
  },

  // Update profile picture
  updateProfilePicture: (profilePictureUrl: string) => {
    return apiClient.post<ApiResponse<User>>('/profile/profile-picture', null, {
      params: { profilePictureUrl }
    })
  }
}

export default profileApi
```

Create `src/stores/profile.ts`:
```typescript
import { defineStore } from 'pinia'
import { ref } from 'vue'
import type { User, HealthProfile } from '@/types'
import profileApi from '@/api/profile'

export const useProfileStore = defineStore('profile', () => {
  // State
  const healthProfile = ref<HealthProfile | null>(null)
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  // Actions
  const fetchHealthProfile = async (): Promise<void> => {
    try {
      isLoading.value = true
      error.value = null

      const response = await profileApi.getHealthProfile()
      healthProfile.value = response.data.data

    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to fetch health profile'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const updateHealthProfile = async (data: Partial<HealthProfile>): Promise<void> => {
    try {
      isLoading.value = true
      error.value = null

      const response = await profileApi.updateHealthProfile(data)
      healthProfile.value = response.data.data

    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to update health profile'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const clearError = (): void => {
    error.value = null
  }

  return {
    // State
    healthProfile,
    isLoading,
    error,
    // Actions
    fetchHealthProfile,
    updateHealthProfile,
    clearError
  }
})
```

### **2. Doctor Service Integration**

Create `src/api/doctors.ts`:
```typescript
import apiClient from './index'
import type { Doctor, ApiResponse } from '@/types'

interface DoctorSearchParams {
  specialization?: string
  city?: string
  state?: string
  language?: string
  minRating?: number
  page?: number
  size?: number
}

const doctorsApi = {
  // Search doctors
  searchDoctors: (params: DoctorSearchParams) => {
    return apiClient.get<ApiResponse<{ content: Doctor[], totalElements: number }>>('/doctors/search', { params })
  },

  // Get all doctors (admin only)
  getAllDoctors: () => {
    return apiClient.get<ApiResponse<Doctor[]>>('/doctors')
  },

  // Get doctor by ID
  getDoctorById: (id: number) => {
    return apiClient.get<ApiResponse<Doctor>>(`/doctors/${id}`)
  },

  // Get doctors by specialization
  getDoctorsBySpecialization: (specialization: string) => {
    return apiClient.get<ApiResponse<Doctor[]>>(`/doctors/by-specialization?specialization=${specialization}`)
  },

  // Create doctor (admin only)
  createDoctor: (doctorData: Partial<Doctor>) => {
    return apiClient.post<ApiResponse<Doctor>>('/doctors', doctorData)
  },

  // Update doctor
  updateDoctor: (id: number, doctorData: Partial<Doctor>) => {
    return apiClient.put<ApiResponse<Doctor>>(`/doctors/${id}`, doctorData)
  },

  // Delete doctor (admin only)
  deleteDoctor: (id: number) => {
    return apiClient.delete<ApiResponse>(`/doctors/${id}`)
  }
}

export default doctorsApi
```

Create `src/stores/doctors.ts`:
```typescript
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Doctor } from '@/types'
import doctorsApi from '@/api/doctors'

export const useDoctorsStore = defineStore('doctors', () => {
  // State
  const doctors = ref<Doctor[]>([])
  const selectedDoctor = ref<Doctor | null>(null)
  const searchResults = ref<Doctor[]>([])
  const totalResults = ref(0)
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  // Getters
  const specializations = computed(() => {
    const specs = doctors.value.map(doctor => doctor.specialization)
    return [...new Set(specs)].sort()
  })

  // Actions
  const searchDoctors = async (params: any): Promise<void> => {
    try {
      isLoading.value = true
      error.value = null

      const response = await doctorsApi.searchDoctors(params)
      searchResults.value = response.data.data.content
      totalResults.value = response.data.data.totalElements

    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to search doctors'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const fetchDoctorById = async (id: number): Promise<void> => {
    try {
      isLoading.value = true
      error.value = null

      const response = await doctorsApi.getDoctorById(id)
      selectedDoctor.value = response.data.data

    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to fetch doctor'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const fetchDoctorsBySpecialization = async (specialization: string): Promise<void> => {
    try {
      isLoading.value = true
      error.value = null

      const response = await doctorsApi.getDoctorsBySpecialization(specialization)
      doctors.value = response.data.data

    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to fetch doctors'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const clearError = (): void => {
    error.value = null
  }

  return {
    // State
    doctors,
    selectedDoctor,
    searchResults,
    totalResults,
    isLoading,
    error,
    // Getters
    specializations,
    // Actions
    searchDoctors,
    fetchDoctorById,
    fetchDoctorsBySpecialization,
    clearError
  }
})
```

### **3. Appointment Service Integration**

Create `src/api/appointments.ts`:
```typescript
import apiClient from './index'
import type { Appointment, AppointmentBookingRequest, AppointmentStatus, ApiResponse } from '@/types'

const appointmentsApi = {
  // Book new appointment
  bookAppointment: (appointmentData: AppointmentBookingRequest) => {
    return apiClient.post<ApiResponse<Appointment>>('/appointments/book', appointmentData)
  },

  // Get user's appointments
  getMyAppointments: () => {
    return apiClient.get<ApiResponse<Appointment[]>>('/appointments/my-appointments')
  },

  // Get doctor's appointments
  getDoctorAppointments: (doctorId: number) => {
    return apiClient.get<ApiResponse<Appointment[]>>(`/appointments/doctor/${doctorId}`)
  },

  // Get upcoming appointments
  getUpcomingAppointments: (userType: string = 'patient') => {
    return apiClient.get<ApiResponse<Appointment[]>>('/appointments/upcoming', {
      params: { userType }
    })
  },

  // Get appointment by ID
  getAppointmentById: (id: number) => {
    return apiClient.get<ApiResponse<Appointment>>(`/appointments/${id}`)
  },

  // Update appointment status
  updateAppointmentStatus: (id: number, status: AppointmentStatus) => {
    return apiClient.put<ApiResponse<Appointment>>(`/appointments/${id}/status`, null, {
      params: { status }
    })
  },

  // Add appointment notes
  addAppointmentNotes: (id: number, notes: string) => {
    return apiClient.put<ApiResponse<Appointment>>(`/appointments/${id}/notes`, null, {
      params: { notes }
    })
  },

  // Cancel appointment
  cancelAppointment: (id: number) => {
    return apiClient.delete<ApiResponse>(`/appointments/${id}`)
  },

  // Get available slots
  getAvailableSlots: (doctorId: number, date: string) => {
    return apiClient.get<ApiResponse<string[]>>('/appointments/available-slots', {
      params: { doctorId, date }
    })
  }
}

export default appointmentsApi
```

Create `src/stores/appointments.ts`:
```typescript
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Appointment, AppointmentBookingRequest, AppointmentStatus } from '@/types'
import appointmentsApi from '@/api/appointments'

export const useAppointmentsStore = defineStore('appointments', () => {
  // State
  const appointments = ref<Appointment[]>([])
  const upcomingAppointments = ref<Appointment[]>([])
  const selectedAppointment = ref<Appointment | null>(null)
  const availableSlots = ref<string[]>([])
  const isLoading = ref(false)
  const isBooking = ref(false)
  const error = ref<string | null>(null)

  // Getters
  const appointmentsByStatus = computed(() => {
    return (status: AppointmentStatus) => 
      appointments.value.filter(apt => apt.status === status)
  })

  const todaysAppointments = computed(() => {
    const today = new Date().toISOString().split('T')[0]
    return appointments.value.filter(apt => 
      apt.appointmentDateTime.startsWith(today)
    )
  })

  // Actions
  const bookAppointment = async (appointmentData: AppointmentBookingRequest): Promise<Appointment> => {
    try {
      isBooking.value = true
      error.value = null

      const response = await appointmentsApi.bookAppointment(appointmentData)
      const newAppointment = response.data.data

      // Add to appointments list
      appointments.value.unshift(newAppointment)
      
      return newAppointment

    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to book appointment'
      throw err
    } finally {
      isBooking.value = false
    }
  }

  const fetchMyAppointments = async (): Promise<void> => {
    try {
      isLoading.value = true
      error.value = null

      const response = await appointmentsApi.getMyAppointments()
      appointments.value = response.data.data

    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to fetch appointments'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const fetchUpcomingAppointments = async (userType?: string): Promise<void> => {
    try {
      isLoading.value = true
      error.value = null

      const response = await appointmentsApi.getUpcomingAppointments(userType)
      upcomingAppointments.value = response.data.data

    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to fetch upcoming appointments'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const fetchAvailableSlots = async (doctorId: number, date: string): Promise<void> => {
    try {
      isLoading.value = true
      error.value = null

      const response = await appointmentsApi.getAvailableSlots(doctorId, date)
      availableSlots.value = response.data.data

    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to fetch available slots'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const updateAppointmentStatus = async (id: number, status: AppointmentStatus): Promise<void> => {
    try {
      isLoading.value = true
      error.value = null

      const response = await appointmentsApi.updateAppointmentStatus(id, status)
      const updatedAppointment = response.data.data

      // Update appointment in list
      const index = appointments.value.findIndex(apt => apt.id === id)
      if (index !== -1) {
        appointments.value[index] = updatedAppointment
      }

    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to update appointment status'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const cancelAppointment = async (id: number): Promise<void> => {
    try {
      isLoading.value = true
      error.value = null

      await appointmentsApi.cancelAppointment(id)
      
      // Remove appointment from list
      appointments.value = appointments.value.filter(apt => apt.id !== id)
      upcomingAppointments.value = upcomingAppointments.value.filter(apt => apt.id !== id)

    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to cancel appointment'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const clearError = (): void => {
    error.value = null
  }

  return {
    // State
    appointments,
    upcomingAppointments,
    selectedAppointment,
    availableSlots,
    isLoading,
    isBooking,
    error,
    // Getters
    appointmentsByStatus,
    todaysAppointments,
    // Actions
    bookAppointment,
    fetchMyAppointments,
    fetchUpcomingAppointments,
    fetchAvailableSlots,
    updateAppointmentStatus,
    cancelAppointment,
    clearError
  }
})
```

### **4. Medication Service Integration**

Create `src/api/medications.ts`:
```typescript
import apiClient from './index'
import type { Medication, MedicationStatus, ApiResponse } from '@/types'

const medicationsApi = {
  // Add new medication
  addMedication: (medicationData: Partial<Medication>) => {
    return apiClient.post<ApiResponse<Medication>>('/medications', medicationData)
  },

  // Get user's medications
  getUserMedications: () => {
    return apiClient.get<ApiResponse<Medication[]>>('/medications')
  },

  // Get active medications
  getActiveMedications: () => {
    return apiClient.get<ApiResponse<Medication[]>>('/medications/active')
  },

  // Get medication by ID
  getMedicationById: (id: number) => {
    return apiClient.get<ApiResponse<Medication>>(`/medications/${id}`)
  },

  // Update medication
  updateMedication: (id: number, medicationData: Partial<Medication>) => {
    return apiClient.put<ApiResponse<Medication>>(`/medications/${id}`, medicationData)
  },

  // Update medication status
  updateMedicationStatus: (id: number, status: MedicationStatus) => {
    return apiClient.put<ApiResponse<Medication>>(`/medications/${id}/status`, null, {
      params: { status }
    })
  },

  // Update remaining quantity
  updateRemainingQuantity: (id: number, quantity: number) => {
    return apiClient.put<ApiResponse<Medication>>(`/medications/${id}/quantity`, null, {
      params: { quantity }
    })
  },

  // Delete medication
  deleteMedication: (id: number) => {
    return apiClient.delete<ApiResponse>(`/medications/${id}`)
  },

  // Get medications needing refill
  getMedicationsNeedingRefill: () => {
    return apiClient.get<ApiResponse<Medication[]>>('/medications/refill-needed')
  },

  // Get medications by status
  getMedicationsByStatus: (status: MedicationStatus) => {
    return apiClient.get<ApiResponse<Medication[]>>('/medications/by-status', {
      params: { status }
    })
  }
}

export default medicationsApi
```

Create `src/stores/medications.ts`:
```typescript
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Medication, MedicationStatus } from '@/types'
import medicationsApi from '@/api/medications'

export const useMedicationsStore = defineStore('medications', () => {
  // State
  const medications = ref<Medication[]>([])
  const activeMedications = ref<Medication[]>([])
  const refillNeeded = ref<Medication[]>([])
  const selectedMedication = ref<Medication | null>(null)
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  // Getters
  const medicationsByStatus = computed(() => {
    return (status: MedicationStatus) =>
      medications.value.filter(med => med.status === status)
  })

  const upcomingRefills = computed(() => {
    return medications.value.filter(med => 
      med.status === 'ACTIVE' && med.remainingQuantity <= med.refillReminder
    )
  })

  const medicationSchedule = computed(() => {
    return activeMedications.value.map(med => ({
      ...med,
      scheduleToday: calculateTodaySchedule(med)
    }))
  })

  // Helper function to calculate today's schedule
  const calculateTodaySchedule = (medication: Medication) => {
    // This is a simplified example - you would implement proper scheduling logic
    const frequency = medication.frequency.toLowerCase()
    if (frequency.includes('once')) return ['08:00']
    if (frequency.includes('twice')) return ['08:00', '20:00']
    if (frequency.includes('three')) return ['08:00', '14:00', '20:00']
    return ['08:00']
  }

  // Actions
  const addMedication = async (medicationData: Partial<Medication>): Promise<Medication> => {
    try {
      isLoading.value = true
      error.value = null

      const response = await medicationsApi.addMedication(medicationData)
      const newMedication = response.data.data

      medications.value.unshift(newMedication)
      if (newMedication.status === 'ACTIVE') {
        activeMedications.value.unshift(newMedication)
      }

      return newMedication

    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to add medication'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const fetchMedications = async (): Promise<void> => {
    try {
      isLoading.value = true
      error.value = null

      const response = await medicationsApi.getUserMedications()
      medications.value = response.data.data

    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to fetch medications'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const fetchActiveMedications = async (): Promise<void> => {
    try {
      isLoading.value = true
      error.value = null

      const response = await medicationsApi.getActiveMedications()
      activeMedications.value = response.data.data

    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to fetch active medications'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const fetchRefillNeeded = async (): Promise<void> => {
    try {
      isLoading.value = true
      error.value = null

      const response = await medicationsApi.getMedicationsNeedingRefill()
      refillNeeded.value = response.data.data

    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to fetch refill needed medications'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const updateMedication = async (id: number, data: Partial<Medication>): Promise<void> => {
    try {
      isLoading.value = true
      error.value = null

      const response = await medicationsApi.updateMedication(id, data)
      const updatedMedication = response.data.data

      // Update medication in lists
      const index = medications.value.findIndex(med => med.id === id)
      if (index !== -1) {
        medications.value[index] = updatedMedication
      }

      const activeIndex = activeMedications.value.findIndex(med => med.id === id)
      if (activeIndex !== -1) {
        if (updatedMedication.status === 'ACTIVE') {
          activeMedications.value[activeIndex] = updatedMedication
        } else {
          activeMedications.value.splice(activeIndex, 1)
        }
      }

    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to update medication'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const updateMedicationStatus = async (id: number, status: MedicationStatus): Promise<void> => {
    try {
      isLoading.value = true
      error.value = null

      const response = await medicationsApi.updateMedicationStatus(id, status)
      const updatedMedication = response.data.data

      // Update medication in lists
      const index = medications.value.findIndex(med => med.id === id)
      if (index !== -1) {
        medications.value[index] = updatedMedication
      }

      // Update active medications list
      const activeIndex = activeMedications.value.findIndex(med => med.id === id)
      if (status === 'ACTIVE' && activeIndex === -1) {
        activeMedications.value.push(updatedMedication)
      } else if (status !== 'ACTIVE' && activeIndex !== -1) {
        activeMedications.value.splice(activeIndex, 1)
      }

    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to update medication status'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const deleteMedication = async (id: number): Promise<void> => {
    try {
      isLoading.value = true
      error.value = null

      await medicationsApi.deleteMedication(id)

      // Remove from all lists
      medications.value = medications.value.filter(med => med.id !== id)
      activeMedications.value = activeMedications.value.filter(med => med.id !== id)
      refillNeeded.value = refillNeeded.value.filter(med => med.id !== id)

    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to delete medication'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const clearError = (): void => {
    error.value = null
  }

  return {
    // State
    medications,
    activeMedications,
    refillNeeded,
    selectedMedication,
    isLoading,
    error,
    // Getters
    medicationsByStatus,
    upcomingRefills,
    medicationSchedule,
    // Actions
    addMedication,
    fetchMedications,
    fetchActiveMedications,
    fetchRefillNeeded,
    updateMedication,
    updateMedicationStatus,
    deleteMedication,
    clearError
  }
})
```

### **5. Health Tracking Service Integration**

Create `src/api/health.ts`:
```typescript
import apiClient from './index'
import type { HealthGoal, HealthTrackingOverview, ApiResponse } from '@/types'

const healthApi = {
  // Get health tracking overview
  getOverview: () => {
    return apiClient.get<ApiResponse<HealthTrackingOverview>>('/healthtracking/overview')
  },

  // Get all health goals
  getGoals: () => {
    return apiClient.get<ApiResponse<HealthGoal[]>>('/healthtracking/goals')
  },

  // Add new health goal
  addGoal: (goalData: Partial<HealthGoal>) => {
    return apiClient.post<ApiResponse<HealthGoal>>('/healthtracking/goals', goalData)
  },

  // Update health goal
  updateGoal: (goalId: number, goalData: Partial<HealthGoal>) => {
    return apiClient.put<ApiResponse<HealthGoal>>(`/healthtracking/goals/${goalId}`, goalData)
  },

  // Delete health goal
  deleteGoal: (goalId: number) => {
    return apiClient.delete<ApiResponse>(`/healthtracking/goals/${goalId}`)
  }
}

export default healthApi
```

Create `src/stores/health.ts`:
```typescript
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { HealthGoal, HealthTrackingOverview, HealthGoalStatus } from '@/types'
import healthApi from '@/api/health'

export const useHealthStore = defineStore('health', () => {
  // State
  const overview = ref<HealthTrackingOverview | null>(null)
  const goals = ref<HealthGoal[]>([])
  const selectedGoal = ref<HealthGoal | null>(null)
  const isLoading = ref(false)
  const error = ref<string | null>(null)

  // Getters
  const goalsByStatus = computed(() => {
    return (status: HealthGoalStatus) =>
      goals.value.filter(goal => goal.status === status)
  })

  const activeGoals = computed(() => goalsByStatus.value('IN_PROGRESS'))
  const completedGoals = computed(() => goalsByStatus.value('COMPLETED'))
  const onTrackGoals = computed(() => goalsByStatus.value('ON_TRACK'))

  const goalProgress = computed(() => {
    return (goal: HealthGoal) => {
      if (goal.targetValue === 0) return 0
      return Math.min((goal.currentValue / goal.targetValue) * 100, 100)
    }
  })

  // Actions
  const fetchOverview = async (): Promise<void> => {
    try {
      isLoading.value = true
      error.value = null

      const response = await healthApi.getOverview()
      overview.value = response.data

    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to fetch health overview'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const fetchGoals = async (): Promise<void> => {
    try {
      isLoading.value = true
      error.value = null

      const response = await healthApi.getGoals()
      goals.value = response.data

    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to fetch health goals'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const addGoal = async (goalData: Partial<HealthGoal>): Promise<HealthGoal> => {
    try {
      isLoading.value = true
      error.value = null

      const response = await healthApi.addGoal(goalData)
      const newGoal = response.data

      goals.value.unshift(newGoal)
      return newGoal

    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to add health goal'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const updateGoal = async (goalId: number, data: Partial<HealthGoal>): Promise<void> => {
    try {
      isLoading.value = true
      error.value = null

      const response = await healthApi.updateGoal(goalId, data)
      const updatedGoal = response.data

      const index = goals.value.findIndex(goal => goal.id === goalId)
      if (index !== -1) {
        goals.value[index] = updatedGoal
      }

    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to update health goal'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const deleteGoal = async (goalId: number): Promise<void> => {
    try {
      isLoading.value = true
      error.value = null

      await healthApi.deleteGoal(goalId)
      goals.value = goals.value.filter(goal => goal.id !== goalId)

    } catch (err: any) {
      error.value = err.response?.data?.message || 'Failed to delete health goal'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const updateGoalProgress = async (goalId: number, currentValue: number): Promise<void> => {
    const goal = goals.value.find(g => g.id === goalId)
    if (!goal) return

    // Determine status based on progress
    let status: HealthGoalStatus = 'IN_PROGRESS'
    if (currentValue >= goal.targetValue) {
      status = 'COMPLETED'
    } else if (currentValue >= goal.targetValue * 0.8) {
      status = 'ON_TRACK'
    }

    await updateGoal(goalId, { currentValue, status })
  }

  const clearError = (): void => {
    error.value = null
  }

  return {
    // State
    overview,
    goals,
    selectedGoal,
    isLoading,
    error,
    // Getters
    goalsByStatus,
    activeGoals,
    completedGoals,
    onTrackGoals,
    goalProgress,
    // Actions
    fetchOverview,
    fetchGoals,
    addGoal,
    updateGoal,
    deleteGoal,
    updateGoalProgress,
    clearError
  }
})
```

---

## 🚨 **Error Handling**

### **1. Global Error Handler**
Create `src/utils/errorHandler.ts`:
```typescript
import { useToast } from 'vue-toastification'

export interface ApiError {
  response?: {
    status: number
    data: {
      success: boolean
      message: string
      errors?: any[]
    }
  }
  message: string
}

export class ErrorHandler {
  private static toast = useToast()

  static handle(error: ApiError, context?: string): void {
    console.error(`Error in ${context}:`, error)

    let message = 'An unexpected error occurred'
    let type: 'error' | 'warning' = 'error'

    if (error.response?.data?.message) {
      message = error.response.data.message
    } else if (error.message) {
      message = error.message
    }

    // Handle specific HTTP status codes
    switch (error.response?.status) {
      case 400:
        type = 'warning'
        message = error.response.data.message || 'Invalid request data'
        break
      case 401:
        message = 'Authentication required. Please log in.'
        // Auto logout will be handled by axios interceptor
        break
      case 403:
        message = 'You do not have permission to perform this action'
        break
      case 404:
        message = 'Requested resource not found'
        break
      case 409:
        type = 'warning'
        message = error.response.data.message || 'Conflict occurred'
        break
      case 422:
        type = 'warning'
        message = 'Validation failed. Please check your input.'
        break
      case 500:
        message = 'Server error. Please try again later.'
        break
      case 503:
        message = 'Service temporarily unavailable. Please try again later.'
        break
    }

    this.toast[type](message)
  }

  static handleValidationErrors(errors: any[]): string[] {
    return errors.map(error => error.message || 'Validation error')
  }

  static isNetworkError(error: ApiError): boolean {
    return !error.response && error.message.includes('Network Error')
  }

  static isTimeoutError(error: ApiError): boolean {
    return error.message.includes('timeout')
  }
}
```

### **2. Form Validation with Error Handling**
Create `src/composables/useFormValidation.ts`:
```typescript
import { ref, computed } from 'vue'
import * as yup from 'yup'

export function useFormValidation<T extends Record<string, any>>(
  schema: yup.ObjectSchema<T>,
  initialValues: T
) {
  const values = ref<T>(initialValues)
  const errors = ref<Record<string, string>>({})
  const isSubmitting = ref(false)

  const isValid = computed(() => {
    return Object.keys(errors.value).length === 0
  })

  const validate = async (): Promise<boolean> => {
    try {
      await schema.validate(values.value, { abortEarly: false })
      errors.value = {}
      return true
    } catch (validationErrors: any) {
      const newErrors: Record<string, string> = {}
      validationErrors.inner.forEach((error: any) => {
        if (error.path) {
          newErrors[error.path] = error.message
        }
      })
      errors.value = newErrors
      return false
    }
  }

  const validateField = async (field: keyof T): Promise<boolean> => {
    try {
      await schema.validateAt(field as string, values.value)
      delete errors.value[field as string]
      return true
    } catch (error: any) {
      errors.value[field as string] = error.message
      return false
    }
  }

  const setFieldValue = (field: keyof T, value: any): void => {
    values.value[field] = value
    // Clear error when user starts typing
    if (errors.value[field as string]) {
      delete errors.value[field as string]
    }
  }

  const setFieldError = (field: keyof T, message: string): void => {
    errors.value[field as string] = message
  }

  const clearErrors = (): void => {
    errors.value = {}
  }

  const reset = (): void => {
    values.value = { ...initialValues }
    errors.value = {}
    isSubmitting.value = false
  }

  return {
    values,
    errors,
    isSubmitting,
    isValid,
    validate,
    validateField,
    setFieldValue,
    setFieldError,
    clearErrors,
    reset
  }
}
```

### **3. Connection Error Handling**
Create `src/composables/useConnectionStatus.ts`:
```typescript
import { ref, onMounted, onUnmounted } from 'vue'
import { useToast } from 'vue-toastification'

export function useConnectionStatus() {
  const isOnline = ref(navigator.onLine)
  const toast = useToast()

  const updateOnlineStatus = (): void => {
    const previousStatus = isOnline.value
    isOnline.value = navigator.onLine

    if (previousStatus !== isOnline.value) {
      if (isOnline.value) {
        toast.success('Connection restored')
      } else {
        toast.error('Connection lost. Some features may not be available.')
      }
    }
  }

  onMounted(() => {
    window.addEventListener('online', updateOnlineStatus)
    window.addEventListener('offline', updateOnlineStatus)
  })

  onUnmounted(() => {
    window.removeEventListener('online', updateOnlineStatus)
    window.removeEventListener('offline', updateOnlineStatus)
  })

  return {
    isOnline
  }
}
```

---

## 🧩 **Component Examples**

### **1. Dashboard Component**

```vue
<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <h1>Welcome back, {{ userProfile?.firstName }}!</h1>
      <div class="stats-grid">
        <StatCard 
          title="Upcoming Appointments" 
          :value="upcomingAppointments.length"
          icon="calendar"
          color="blue"
        />
        <StatCard 
          title="Active Medications" 
          :value="activeMedications.length"
          icon="pill"
          color="green"
        />
        <StatCard 
          title="Health Goals" 
          :value="activeGoals.length"
          icon="target"
          color="purple"
        />
      </div>
    </div>

    <div class="dashboard-content">
      <div class="appointments-section">
        <h2>Upcoming Appointments</h2>
        <AppointmentCard 
          v-for="appointment in upcomingAppointments" 
          :key="appointment.id"
          :appointment="appointment"
          @reschedule="handleReschedule"
          @cancel="handleCancel"
        />
      </div>

      <div class="medications-section">
        <h2>Today's Medications</h2>
        <MedicationReminder 
          v-for="medication in todaysMedications" 
          :key="medication.id"
          :medication="medication"
          @mark-taken="handleMedicationTaken"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useUserProfileStore } from '@/stores/userProfile'
import { useAppointmentStore } from '@/stores/appointment'
import { useMedicationStore } from '@/stores/medication'
import { useHealthTrackingStore } from '@/stores/healthTracking'
import StatCard from '@/components/StatCard.vue'
import AppointmentCard from '@/components/AppointmentCard.vue'
import MedicationReminder from '@/components/MedicationReminder.vue'

const userProfileStore = useUserProfileStore()
const appointmentStore = useAppointmentStore()
const medicationStore = useMedicationStore()
const healthTrackingStore = useHealthTrackingStore()

const userProfile = computed(() => userProfileStore.userProfile)
const upcomingAppointments = computed(() => appointmentStore.upcomingAppointments)
const activeMedications = computed(() => medicationStore.activeMedications)
const activeGoals = computed(() => healthTrackingStore.activeGoals)

const todaysMedications = computed(() => {
  return medicationStore.medications.filter(med => 
    med.schedule?.some(schedule => schedule.time === new Date().toISOString().split('T')[0])
  )
})

onMounted(async () => {
  await Promise.all([
    userProfileStore.fetchProfile(),
    appointmentStore.fetchUpcomingAppointments(),
    medicationStore.fetchMedications(),
    healthTrackingStore.fetchGoals()
  ])
})

const handleReschedule = (appointmentId) => {
  // Navigate to reschedule page
  router.push(`/appointments/${appointmentId}/reschedule`)
}

const handleCancel = async (appointmentId) => {
  await appointmentStore.cancelAppointment(appointmentId)
}

const handleMedicationTaken = async (medicationId) => {
  await medicationStore.markMedicationTaken(medicationId)
}
</script>

<style scoped>
.dashboard {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.dashboard-header h1 {
  font-size: 2rem;
  color: #2c3e50;
  margin-bottom: 1.5rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1rem;
  margin-bottom: 2rem;
}

.dashboard-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
}

@media (max-width: 768px) {
  .dashboard-content {
    grid-template-columns: 1fr;
  }
}
</style>
```

### **2. Doctor Search Component**

```vue
<template>
  <div class="doctor-search">
    <div class="search-filters">
      <div class="search-bar">
        <input 
          v-model="searchQuery" 
          type="text" 
          placeholder="Search doctors by name or specialty..."
          @input="debouncedSearch"
        />
      </div>
      
      <div class="filters">
        <select v-model="selectedSpecialty" @change="applyFilters">
          <option value="">All Specialties</option>
          <option v-for="specialty in specialties" :key="specialty" :value="specialty">
            {{ specialty }}
          </option>
        </select>
        
        <select v-model="selectedLocation" @change="applyFilters">
          <option value="">All Locations</option>
          <option v-for="location in locations" :key="location" :value="location">
            {{ location }}
          </option>
        </select>
        
        <div class="rating-filter">
          <label>Min Rating:</label>
          <star-rating v-model="minRating" @change="applyFilters" />
        </div>
      </div>
    </div>

    <div class="search-results">
      <div v-if="doctorStore.loading" class="loading">
        <div class="spinner"></div>
        <p>Searching doctors...</p>
      </div>
      
      <div v-else-if="doctorStore.error" class="error">
        <p>{{ doctorStore.error }}</p>
        <button @click="retrySearch">Retry Search</button>
      </div>
      
      <div v-else-if="filteredDoctors.length === 0" class="no-results">
        <p>No doctors found matching your criteria.</p>
      </div>
      
      <div v-else class="doctors-grid">
        <DoctorCard 
          v-for="doctor in filteredDoctors" 
          :key="doctor.id"
          :doctor="doctor"
          @book-appointment="handleBookAppointment"
          @view-profile="handleViewProfile"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useDoctorStore } from '@/stores/doctor'
import { debounce } from 'lodash-es'
import DoctorCard from '@/components/DoctorCard.vue'
import StarRating from '@/components/StarRating.vue'

const router = useRouter()
const doctorStore = useDoctorStore()

const searchQuery = ref('')
const selectedSpecialty = ref('')
const selectedLocation = ref('')
const minRating = ref(0)

const filteredDoctors = computed(() => {
  let doctors = doctorStore.doctors
  
  if (searchQuery.value) {
    doctors = doctors.filter(doctor => 
      doctor.name.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
      doctor.specializations.some(spec => 
        spec.toLowerCase().includes(searchQuery.value.toLowerCase())
      )
    )
  }
  
  if (selectedSpecialty.value) {
    doctors = doctors.filter(doctor => 
      doctor.specializations.includes(selectedSpecialty.value)
    )
  }
  
  if (selectedLocation.value) {
    doctors = doctors.filter(doctor => 
      doctor.location.includes(selectedLocation.value)
    )
  }
  
  if (minRating.value > 0) {
    doctors = doctors.filter(doctor => doctor.rating >= minRating.value)
  }
  
  return doctors
})

const specialties = computed(() => {
  return [...new Set(doctorStore.doctors.flatMap(d => d.specializations))]
})

const locations = computed(() => {
  return [...new Set(doctorStore.doctors.map(d => d.location))]
})

const debouncedSearch = debounce(() => {
  applyFilters()
}, 300)

onMounted(async () => {
  await doctorStore.fetchDoctors()
})

const applyFilters = () => {
  // Filters are applied through computed property
  // This method can be used for additional logic if needed
}

const retrySearch = () => {
  doctorStore.fetchDoctors()
}

const handleBookAppointment = (doctor) => {
  router.push(`/appointments/book/${doctor.id}`)
}

const handleViewProfile = (doctor) => {
  router.push(`/doctors/${doctor.id}`)
}
</script>
```

### **3. Appointment Booking Component**

```vue
<template>
  <div class="appointment-booking">
    <div class="booking-header">
      <h1>Book Appointment</h1>
      <div v-if="doctor" class="doctor-info">
        <img :src="doctor.profileImage" :alt="doctor.name" />
        <div>
          <h2>{{ doctor.name }}</h2>
          <p>{{ doctor.specializations.join(', ') }}</p>
          <div class="rating">
            <star-rating :value="doctor.rating" readonly />
            <span>({{ doctor.reviewCount }} reviews)</span>
          </div>
        </div>
      </div>
    </div>

    <form @submit.prevent="submitBooking" class="booking-form">
      <div class="form-section">
        <h3>Appointment Type</h3>
        <div class="appointment-types">
          <label v-for="type in appointmentTypes" :key="type.value">
            <input 
              type="radio" 
              v-model="bookingForm.type" 
              :value="type.value"
            />
            <span>{{ type.label }}</span>
            <small>{{ type.description }}</small>
          </label>
        </div>
      </div>

      <div class="form-section">
        <h3>Select Date & Time</h3>
        <div class="date-selector">
          <input 
            type="date" 
            v-model="bookingForm.date"
            :min="minDate"
            :max="maxDate"
            @change="fetchAvailableSlots"
          />
        </div>
        
        <div v-if="availableSlots.length > 0" class="time-slots">
          <h4>Available Times</h4>
          <div class="slots-grid">
            <button 
              v-for="slot in availableSlots" 
              :key="slot.time"
              type="button"
              :class="{ 'selected': bookingForm.time === slot.time }"
              @click="selectTimeSlot(slot.time)"
            >
              {{ formatTime(slot.time) }}
            </button>
          </div>
        </div>
      </div>

      <div class="form-section">
        <h3>Appointment Details</h3>
        <div class="form-group">
          <label for="reason">Reason for Visit</label>
          <textarea 
            id="reason"
            v-model="bookingForm.reason"
            placeholder="Please describe your symptoms or reason for the appointment..."
            required
          ></textarea>
        </div>
        
        <div class="form-group">
          <label for="notes">Additional Notes (Optional)</label>
          <textarea 
            id="notes"
            v-model="bookingForm.notes"
            placeholder="Any additional information for the doctor..."
          ></textarea>
        </div>
      </div>

      <div class="form-actions">
        <button type="button" @click="goBack" class="btn-secondary">
          Cancel
        </button>
        <button 
          type="submit" 
          :disabled="!isFormValid || appointmentStore.loading"
          class="btn-primary"
        >
          {{ appointmentStore.loading ? 'Booking...' : 'Book Appointment' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAppointmentStore } from '@/stores/appointment'
import { useDoctorStore } from '@/stores/doctor'
import StarRating from '@/components/StarRating.vue'

const route = useRoute()
const router = useRouter()
const appointmentStore = useAppointmentStore()
const doctorStore = useDoctorStore()

const doctorId = route.params.doctorId
const doctor = computed(() => doctorStore.selectedDoctor)
const availableSlots = ref([])

const bookingForm = ref({
  type: 'in-person',
  date: '',
  time: '',
  reason: '',
  notes: ''
})

const appointmentTypes = [
  {
    value: 'in-person',
    label: 'In-Person Consultation',
    description: 'Visit the doctor at their clinic'
  },
  {
    value: 'video',
    label: 'Video Consultation',
    description: 'Online video call with the doctor'
  },
  {
    value: 'phone',
    label: 'Phone Consultation',
    description: 'Phone call with the doctor'
  }
]

const minDate = computed(() => {
  const today = new Date()
  return today.toISOString().split('T')[0]
})

const maxDate = computed(() => {
  const future = new Date()
  future.setMonth(future.getMonth() + 3)
  return future.toISOString().split('T')[0]
})

const isFormValid = computed(() => {
  return bookingForm.value.type && 
         bookingForm.value.date && 
         bookingForm.value.time && 
         bookingForm.value.reason.trim()
})

onMounted(async () => {
  if (doctorId) {
    await doctorStore.fetchDoctorById(doctorId)
  }
})

const fetchAvailableSlots = async () => {
  if (bookingForm.value.date && doctorId) {
    availableSlots.value = await appointmentStore.fetchAvailableSlots(
      doctorId, 
      bookingForm.value.date
    )
  }
}

const selectTimeSlot = (time) => {
  bookingForm.value.time = time
}

const formatTime = (time) => {
  return new Date(`2000-01-01T${time}`).toLocaleTimeString('en-US', {
    hour: 'numeric',
    minute: '2-digit',
    hour12: true
  })
}

const submitBooking = async () => {
  try {
    const appointmentData = {
      doctorId: doctorId,
      date: bookingForm.value.date,
      time: bookingForm.value.time,
      type: bookingForm.value.type,
      reason: bookingForm.value.reason,
      notes: bookingForm.value.notes
    }
    
    const result = await appointmentStore.bookAppointment(appointmentData)
    
    if (result.success) {
      router.push(`/appointments/${result.appointmentId}/confirmation`)
    }
  } catch (error) {
    console.error('Booking failed:', error)
  }
}

const goBack = () => {
  router.back()
}
</script>
```

---

## 🧭 **Router Configuration**

### **1. Main Router Setup**

Create `src/router/index.js`:

```javascript
import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

// Layouts
import DefaultLayout from '@/layouts/DefaultLayout.vue'
import AuthLayout from '@/layouts/AuthLayout.vue'
import DashboardLayout from '@/layouts/DashboardLayout.vue'

// Views
import Home from '@/views/Home.vue'
import Login from '@/views/auth/Login.vue'
import Register from '@/views/auth/Register.vue'
import ForgotPassword from '@/views/auth/ForgotPassword.vue'
import Dashboard from '@/views/Dashboard.vue'
import Profile from '@/views/profile/Profile.vue'
import ProfileEdit from '@/views/profile/ProfileEdit.vue'
import Doctors from '@/views/doctors/Doctors.vue'
import DoctorDetail from '@/views/doctors/DoctorDetail.vue'
import Appointments from '@/views/appointments/Appointments.vue'
import AppointmentBook from '@/views/appointments/AppointmentBook.vue'
import AppointmentDetail from '@/views/appointments/AppointmentDetail.vue'
import Medications from '@/views/medications/Medications.vue'
import MedicationDetail from '@/views/medications/MedicationDetail.vue'
import HealthTracking from '@/views/health/HealthTracking.vue'
import HealthGoals from '@/views/health/HealthGoals.vue'
import NotFound from '@/views/NotFound.vue'

const routes = [
  // Public routes
  {
    path: '/',
    component: DefaultLayout,
    children: [
      {
        path: '',
        name: 'Home',
        component: Home
      }
    ]
  },
  
  // Auth routes
  {
    path: '/auth',
    component: AuthLayout,
    meta: { requiresGuest: true },
    children: [
      {
        path: 'login',
        name: 'Login',
        component: Login
      },
      {
        path: 'register',
        name: 'Register',
        component: Register
      },
      {
        path: 'forgot-password',
        name: 'ForgotPassword',
        component: ForgotPassword
      }
    ]
  },
  
  // Protected routes
  {
    path: '/app',
    component: DashboardLayout,
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: Dashboard
      },
      
      // Profile routes
      {
        path: 'profile',
        name: 'Profile',
        component: Profile
      },
      {
        path: 'profile/edit',
        name: 'ProfileEdit',
        component: ProfileEdit
      },
      
      // Doctor routes
      {
        path: 'doctors',
        name: 'Doctors',
        component: Doctors
      },
      {
        path: 'doctors/:id',
        name: 'DoctorDetail',
        component: DoctorDetail,
        props: true
      },
      
      // Appointment routes
      {
        path: 'appointments',
        name: 'Appointments',
        component: Appointments
      },
      {
        path: 'appointments/book/:doctorId',
        name: 'AppointmentBook',
        component: AppointmentBook,
        props: true
      },
      {
        path: 'appointments/:id',
        name: 'AppointmentDetail',
        component: AppointmentDetail,
        props: true
      },
      
      // Medication routes
      {
        path: 'medications',
        name: 'Medications',
        component: Medications
      },
      {
        path: 'medications/:id',
        name: 'MedicationDetail',
        component: MedicationDetail,
        props: true
      },
      
      // Health tracking routes
      {
        path: 'health',
        name: 'HealthTracking',
        component: HealthTracking
      },
      {
        path: 'health/goals',
        name: 'HealthGoals',
        component: HealthGoals
      }
    ]
  },
  
  // Catch all - 404
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFound
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  }
})

// Navigation guards
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()
  
  // Check if route requires authentication
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }
  
  // Check if route requires guest (not authenticated)
  if (to.meta.requiresGuest && authStore.isAuthenticated) {
    next({ name: 'Dashboard' })
    return
  }
  
  // Validate token if authenticated
  if (authStore.isAuthenticated && !authStore.user) {
    try {
      await authStore.fetchUser()
    } catch (error) {
      authStore.logout()
      next({ name: 'Login' })
      return
    }
  }
  
  next()
})

// Global error handling
router.onError((error) => {
  console.error('Router error:', error)
})

export default router
```

### **2. Layout Components**

Create `src/layouts/DefaultLayout.vue`:

```vue
<template>
  <div class="default-layout">
    <AppHeader />
    <main class="main-content">
      <RouterView />
    </main>
    <AppFooter />
  </div>
</template>

<script setup>
import AppHeader from '@/components/layout/AppHeader.vue'
import AppFooter from '@/components/layout/AppFooter.vue'
</script>

<style scoped>
.default-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding: 2rem 0;
}
</style>
```

Create `src/layouts/DashboardLayout.vue`:

```vue
<template>
  <div class="dashboard-layout">
    <DashboardSidebar />
    <div class="main-container">
      <DashboardHeader />
      <main class="main-content">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<script setup>
import DashboardSidebar from '@/components/layout/DashboardSidebar.vue'
import DashboardHeader from '@/components/layout/DashboardHeader.vue'
</script>

<style scoped>
.dashboard-layout {
  display: flex;
  min-height: 100vh;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  padding: 2rem;
  background-color: #f8f9fa;
}
</style>
```

---

## 🧪 **Testing**

### **1. Unit Testing Setup**

Install testing dependencies:

```bash
npm install --save-dev @vue/test-utils vitest jsdom @vitest/ui
```

Update `vite.config.js`:

```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  test: {
    environment: 'jsdom',
    globals: true,
    setupFiles: ['./src/test/setup.js']
  }
})
```

Create `src/test/setup.js`:

```javascript
import { vi } from 'vitest'

// Mock localStorage
const localStorageMock = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn(),
}
global.localStorage = localStorageMock

// Mock sessionStorage
const sessionStorageMock = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn(),
}
global.sessionStorage = sessionStorageMock

// Mock window.matchMedia
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: vi.fn().mockImplementation(query => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: vi.fn(),
    removeListener: vi.fn(),
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    dispatchEvent: vi.fn(),
  })),
})
```

### **2. Store Testing Example**

Create `src/stores/__tests__/auth.test.js`:

```javascript
import { setActivePinia, createPinia } from 'pinia'
import { beforeEach, describe, it, expect, vi } from 'vitest'
import { useAuthStore } from '../auth'
import * as authApi from '@/api/auth'

// Mock the API
vi.mock('@/api/auth')

describe('Auth Store', () => {
  let store

  beforeEach(() => {
    setActivePinia(createPinia())
    store = useAuthStore()
    vi.clearAllMocks()
  })

  it('should login successfully', async () => {
    const mockResponse = {
      token: 'mock-token',
      user: { id: 1, email: 'test@example.com' }
    }
    
    authApi.login.mockResolvedValue(mockResponse)

    await store.login({ email: 'test@example.com', password: 'password' })

    expect(store.isAuthenticated).toBe(true)
    expect(store.token).toBe('mock-token')
    expect(store.user).toEqual(mockResponse.user)
    expect(localStorage.setItem).toHaveBeenCalledWith('token', 'mock-token')
  })

  it('should handle login error', async () => {
    const mockError = new Error('Invalid credentials')
    authApi.login.mockRejectedValue(mockError)

    await expect(store.login({ email: 'test@example.com', password: 'wrong' }))
      .rejects.toThrow('Invalid credentials')
    
    expect(store.isAuthenticated).toBe(false)
    expect(store.token).toBeNull()
  })

  it('should logout successfully', () => {
    // Setup authenticated state
    store.token = 'mock-token'
    store.user = { id: 1, email: 'test@example.com' }

    store.logout()

    expect(store.isAuthenticated).toBe(false)
    expect(store.token).toBeNull()
    expect(store.user).toBeNull()
    expect(localStorage.removeItem).toHaveBeenCalledWith('token')
  })
})
```

### **3. Component Testing Example**

Create `src/components/__tests__/DoctorCard.test.js`:

```javascript
import { mount } from '@vue/test-utils'
import { describe, it, expect } from 'vitest'
import DoctorCard from '../DoctorCard.vue'

describe('DoctorCard', () => {
  const mockDoctor = {
    id: 1,
    name: 'Dr. John Smith',
    specializations: ['Cardiology', 'Internal Medicine'],
    rating: 4.5,
    reviewCount: 123,
    location: 'New York, NY',
    profileImage: 'https://example.com/doctor.jpg'
  }

  it('renders doctor information correctly', () => {
    const wrapper = mount(DoctorCard, {
      props: { doctor: mockDoctor }
    })

    expect(wrapper.text()).toContain('Dr. John Smith')
    expect(wrapper.text()).toContain('Cardiology, Internal Medicine')
    expect(wrapper.text()).toContain('New York, NY')
    expect(wrapper.find('img').attributes('src')).toBe(mockDoctor.profileImage)
  })

  it('emits book-appointment event when button clicked', async () => {
    const wrapper = mount(DoctorCard, {
      props: { doctor: mockDoctor }
    })

    await wrapper.find('[data-testid="book-appointment"]').trigger('click')
    
    expect(wrapper.emitted('book-appointment')).toBeTruthy()
    expect(wrapper.emitted('book-appointment')[0]).toEqual([mockDoctor])
  })

  it('emits view-profile event when profile button clicked', async () => {
    const wrapper = mount(DoctorCard, {
      props: { doctor: mockDoctor }
    })

    await wrapper.find('[data-testid="view-profile"]').trigger('click')
    
    expect(wrapper.emitted('view-profile')).toBeTruthy()
    expect(wrapper.emitted('view-profile')[0]).toEqual([mockDoctor])
  })
})
```

### **4. E2E Testing with Playwright**

Install Playwright:

```bash
npm install --save-dev @playwright/test
npx playwright install
```

Create `tests/e2e/auth.spec.js`:

```javascript
import { test, expect } from '@playwright/test'

test.describe('Authentication Flow', () => {
  test('should login successfully', async ({ page }) => {
    await page.goto('/')
    
    // Navigate to login
    await page.click('text=Login')
    
    // Fill login form
    await page.fill('input[type="email"]', 'test@example.com')
    await page.fill('input[type="password"]', 'password123')
    
    // Submit form
    await page.click('button[type="submit"]')
    
    // Verify redirect to dashboard
    await expect(page).toHaveURL('/app/dashboard')
    await expect(page.locator('h1')).toContainText('Welcome back')
  })

  test('should show error for invalid credentials', async ({ page }) => {
    await page.goto('/auth/login')
    
    // Fill with invalid credentials
    await page.fill('input[type="email"]', 'invalid@example.com')
    await page.fill('input[type="password"]', 'wrongpassword')
    
    // Submit form
    await page.click('button[type="submit"]')
    
    // Verify error message
    await expect(page.locator('.error-message')).toBeVisible()
    await expect(page.locator('.error-message')).toContainText('Invalid credentials')
  })
})
```

### **5. Package.json Testing Scripts**

Add to `package.json`:

```json
{
  "scripts": {
    "test": "vitest",
    "test:ui": "vitest --ui",
    "test:coverage": "vitest --coverage",
    "test:e2e": "playwright test",
    "test:e2e:ui": "playwright test --ui"
  }
}
```

---

## 🚀 **Deployment**

### **1. Build Configuration**

Update `vite.config.js` for production:

```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  build: {
    outDir: 'dist',
    assetsDir: 'assets',
    sourcemap: true,
    rollupOptions: {
      output: {
        manualChunks: {
          vendor: ['vue', 'vue-router', 'pinia'],
          ui: ['axios', 'lodash-es']
        }
      }
    }
  },
  define: {
    __VUE_PROD_DEVTOOLS__: false
  }
})
```

### **2. Environment Configuration**

Create `.env.production`:

```env
VITE_API_BASE_URL=https://api.smartcare.com
VITE_APP_NAME=Smart Care Services
VITE_APP_ENV=production
```

### **3. Docker Deployment**

Create `Dockerfile`:

```dockerfile
# Build stage
FROM node:18-alpine as build-stage

WORKDIR /app

# Copy package files
COPY package*.json ./
RUN npm ci --only=production

# Copy source code
COPY . .

# Build application
RUN npm run build

# Production stage
FROM nginx:alpine as production-stage

# Copy built application
COPY --from=build-stage /app/dist /usr/share/nginx/html

# Copy nginx configuration
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

Create `nginx.conf`:

```nginx
events {
    worker_connections 1024;
}

http {
    include       /etc/nginx/mime.types;
    default_type  application/octet-stream;

    server {
        listen 80;
        server_name localhost;
        root /usr/share/nginx/html;
        index index.html;

        # Handle client-side routing
        location / {
            try_files $uri $uri/ /index.html;
        }

        # API proxy (if needed)
        location /api/ {
            proxy_pass http://backend:8080;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }

        # Static assets caching
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
            expires 1y;
            add_header Cache-Control "public, immutable";
        }
    }
}
```

### **4. Vercel Deployment**

Create `vercel.json`:

```json
{
  "builds": [
    {
      "src": "package.json",
      "use": "@vercel/static-build",
      "config": {
        "distDir": "dist"
      }
    }
  ],
  "routes": [
    {
      "src": "/api/(.*)",
      "dest": "https://your-backend-api.com/api/$1"
    },
    {
      "src": "/(.*)",
      "dest": "/index.html"
    }
  ]
}
```

### **5. Netlify Deployment**

Create `netlify.toml`:

```toml
[build]
  publish = "dist"
  command = "npm run build"

[[redirects]]
  from = "/api/*"
  to = "https://your-backend-api.com/api/:splat"
  status = 200

[[redirects]]
  from = "/*"
  to = "/index.html"
  status = 200
```

### **6. GitHub Actions CI/CD**

Create `.github/workflows/deploy.yml`:

```yaml
name: Deploy to Production

on:
  push:
    branches: [ main ]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
          cache: 'npm'
      
      - name: Install dependencies
        run: npm ci
      
      - name: Run tests
        run: npm run test:coverage
      
      - name: Run E2E tests
        run: npm run test:e2e

  build-and-deploy:
    needs: test
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      
      - name: Setup Node.js
        uses: actions/setup-node@v3
        with:
          node-version: '18'
          cache: 'npm'
      
      - name: Install dependencies
        run: npm ci
      
      - name: Build application
        run: npm run build
        env:
          VITE_API_BASE_URL: ${{ secrets.API_BASE_URL }}
      
      - name: Deploy to Vercel
        uses: vercel/action@v2
        with:
          vercel-token: ${{ secrets.VERCEL_TOKEN }}
          vercel-org-id: ${{ secrets.VERCEL_ORG_ID }}
          vercel-project-id: ${{ secrets.VERCEL_PROJECT_ID }}
```

---

## 🛠️ **Troubleshooting**

### **1. Common Issues & Solutions**

#### **CORS Issues**

**Problem**: Browser blocks API requests due to CORS policy.

**Solution**:
```javascript
// In your development environment, use a proxy
// vite.config.js
export default defineConfig({
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      }
    }
  }
})
```

#### **Authentication Token Expiry**

**Problem**: User gets logged out unexpectedly.

**Solution**: Implement token refresh logic:
```javascript
// In your axios interceptor
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      const authStore = useAuthStore()
      try {
        await authStore.refreshToken()
        // Retry original request
        return api.request(error.config)
      } catch (refreshError) {
        authStore.logout()
        router.push('/auth/login')
      }
    }
    return Promise.reject(error)
  }
)
```

#### **Route Protection Issues**

**Problem**: Users can access protected routes without authentication.

**Solution**: Ensure navigation guards are properly configured:
```javascript
router.beforeEach(async (to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }
  
  next()
})
```

### **2. Performance Optimization**

#### **Bundle Size Issues**

**Problem**: Large bundle sizes affecting load times.

**Solutions**:
```javascript
// 1. Lazy load routes
const Dashboard = () => import('@/views/Dashboard.vue')

// 2. Tree shaking for libraries
import { debounce } from 'lodash-es'

// 3. Code splitting in vite.config.js
export default defineConfig({
  build: {
    rollupOptions: {
      output: {
        manualChunks: {
          vendor: ['vue', 'vue-router', 'pinia'],
          ui: ['axios']
        }
      }
    }
  }
})
```

#### **API Performance**

**Problem**: Slow API responses affecting user experience.

**Solutions**:
```javascript
// 1. Implement request cancellation
const controller = new AbortController()
const response = await api.get('/doctors', {
  signal: controller.signal
})

// 2. Add loading states
const { data, loading, error, execute } = useAsyncData(
  'doctors',
  () => doctorApi.fetchDoctors()
)

// 3. Implement caching
const cache = new Map()
const getCachedData = (key) => {
  if (cache.has(key)) {
    return cache.get(key)
  }
  // Fetch and cache
}
```

### **3. Development Issues**

#### **Hot Module Replacement (HMR) Problems**

**Problem**: Changes not reflecting in development.

**Solutions**:
```javascript
// 1. Check vite.config.js
export default defineConfig({
  server: {
    hmr: {
      overlay: true
    }
  }
})

// 2. Clear browser cache and restart dev server
npm run dev -- --force
```

#### **Import Path Issues**

**Problem**: Module resolution errors.

**Solution**: Ensure proper alias configuration:
```javascript
// vite.config.js
export default defineConfig({
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  }
})

// tsconfig.json (if using TypeScript)
{
  "compilerOptions": {
    "paths": {
      "@/*": ["./src/*"]
    }
  }
}
```

### **4. Production Issues**

#### **Environment Variables Not Working**

**Problem**: Environment variables undefined in production.

**Solution**: Ensure proper naming and build configuration:
```javascript
// Use VITE_ prefix for client-side variables
VITE_API_BASE_URL=https://api.example.com

// Access in code
const apiUrl = import.meta.env.VITE_API_BASE_URL
```

#### **Routing Issues in Production**

**Problem**: Direct URL access returns 404.

**Solution**: Configure server for SPA routing:
```nginx
# Nginx configuration
location / {
    try_files $uri $uri/ /index.html;
}
```

### **5. Debugging Tools**

#### **Vue DevTools**

Install and configure Vue DevTools for debugging:
```bash
# Install browser extension
# Chrome: Vue.js devtools
# Firefox: Vue.js devtools
```

#### **Network Debugging**

Use browser developer tools to debug API calls:
```javascript
// Add request/response logging
api.interceptors.request.use(request => {
  console.log('Starting Request:', request)
  return request
})

api.interceptors.response.use(
  response => {
    console.log('Response:', response)
    return response
  },
  error => {
    console.error('Request Error:', error)
    return Promise.reject(error)
  }
)
```

### **6. Quick Fixes Checklist**

When encountering issues, check:

1. **✅ Backend is running** on `http://localhost:8080`
2. **✅ API endpoints** are accessible via browser/Postman
3. **✅ CORS** is configured properly on backend
4. **✅ Environment variables** are properly set
5. **✅ Authentication tokens** are valid and not expired
6. **✅ Network connectivity** is working
7. **✅ Browser console** for error messages
8. **✅ Network tab** for failed requests
9. **✅ Vue DevTools** for state inspection
10. **✅ Server logs** for backend errors

---

## 📋 **Final Checklist**

### **✅ Frontend Setup Complete**
- [ ] Vue 3 project created with Vite
- [ ] All dependencies installed (Vue Router, Pinia, Axios, etc.)
- [ ] Project structure organized
- [ ] Environment variables configured

### **✅ API Integration Complete**
- [ ] Axios configured with interceptors
- [ ] Base API URL and endpoints defined
- [ ] Error handling implemented
- [ ] Request/response logging added

### **✅ Store Management Complete**
- [ ] Pinia stores created for all services
- [ ] Authentication store with JWT handling
- [ ] All API calls integrated in stores
- [ ] State management working properly

### **✅ Components & Views Complete**
- [ ] Layout components created
- [ ] Core components implemented
- [ ] Views for all major features
- [ ] Responsive design applied

### **✅ Routing Complete**
- [ ] Vue Router configured
- [ ] Navigation guards implemented
- [ ] Protected routes working
- [ ] Redirect logic in place

### **✅ Testing Setup Complete**
- [ ] Unit testing with Vitest
- [ ] Component testing with Vue Test Utils
- [ ] E2E testing with Playwright
- [ ] Test coverage configured

### **✅ Deployment Ready**
- [ ] Build configuration optimized
- [ ] Environment-specific configs
- [ ] Deployment files created
- [ ] CI/CD pipeline configured

### **✅ Documentation Complete**
- [ ] Integration instructions comprehensive
- [ ] Code examples provided
- [ ] Troubleshooting guide included
- [ ] Best practices documented

---

**🎉 Congratulations! Your Vue 3 frontend is now fully integrated with the Smart Care Services backend. You have a complete healthcare management system ready for development and deployment.**

**📞 Need Help?**
- Check the troubleshooting section for common issues
- Review the code examples for implementation details
- Ensure backend services are running before testing frontend
- Use browser developer tools for debugging API calls

**🚀 Next Steps:**
1. Run the backend: `./mvnw spring-boot:run`
2. Start the frontend: `npm run dev`
3. Open `http://localhost:5173` in your browser
4. Begin building your healthcare application!

---

**📝 Progress Update: Web Integration Instructions**

## ✅ **Completed Sections:**
1. **Project Overview** - Complete backend service overview and configuration ✅
2. **Prerequisites** - System requirements and verification commands ✅
3. **Vue 3 Project Setup** - Complete project creation with recommended dependencies ✅
4. **API Integration Guide** - Axios configuration with interceptors and error handling ✅
5. **Authentication & Security** - JWT token management and auth store ✅
6. **Service Integrations** - All 5 major services with stores and APIs ✅
7. **Error Handling** - Comprehensive error handling strategies ✅
8. **Component Examples** - Dashboard, Doctor Search, Appointment Booking components ✅
9. **Router Configuration** - Complete routing setup with guards and layouts ✅
10. **Testing** - Unit, component, and E2E testing setup ✅
11. **Deployment** - Multiple deployment options and CI/CD ✅
12. **Troubleshooting** - Comprehensive issue resolution guide ✅

## 🎯 **All Sections Complete!**

This comprehensive guide now provides everything needed to successfully integrate a Vue 3 frontend with the Smart Care Services backend. The documentation includes practical examples, best practices, and solutions for common challenges developers may encounter.
