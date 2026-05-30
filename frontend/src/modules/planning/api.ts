import axios from 'axios'
import { apiClient } from '@/shared/api/client'
import type {
  CalendarWorkoutResponse,
  ExerciseDto,
  StrengthProgressPointResponse,
  TrainingPlanRequest,
  TrainingPlanResponse,
  WeightEntryRequest,
  WeightProgressPointResponse,
} from './types'

const legacyClient = axios.create({
  headers: { 'Content-Type': 'application/json' },
})

function asArray<T>(data: unknown): T[] {
  if (Array.isArray(data)) {
    return data
  }

  if (data && typeof data === 'object') {
    const wrapped = data as { content?: unknown; data?: unknown; plans?: unknown }
    if (Array.isArray(wrapped.content)) {
      return wrapped.content as T[]
    }
    if (Array.isArray(wrapped.data)) {
      return wrapped.data as T[]
    }
    if (Array.isArray(wrapped.plans)) {
      return wrapped.plans as T[]
    }
  }

  return []
}

function normalizePlan(plan: TrainingPlanResponse): TrainingPlanResponse {
  return {
    ...plan,
    exercises: Array.isArray(plan.exercises) ? plan.exercises : [],
  }
}

legacyClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('forma_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

legacyClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('forma_token')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export const planningApi = {
  async getPlans() {
    const { data } = await legacyClient.get<unknown>('/plans')
    return asArray<TrainingPlanResponse>(data).map(normalizePlan)
  },

  async createPlan(payload: TrainingPlanRequest) {
    const { data } = await legacyClient.post<TrainingPlanResponse>('/plans', payload)
    return normalizePlan(data)
  },

  async getPlan(id: number) {
    const { data } = await legacyClient.get<TrainingPlanResponse>(`/plans/${id}`)
    return normalizePlan(data)
  },

  async updatePlan(id: number, payload: TrainingPlanRequest) {
    const { data } = await apiClient.put<TrainingPlanResponse>(`/plans/${id}`, payload)
    return normalizePlan(data)
  },

  async deletePlan(id: number) {
    await apiClient.delete(`/plans/${id}`)
  },

  async getExercises(search?: string) {
    const { data } = await apiClient.get<ExerciseDto[]>('/exercises', {
      params: search ? { search } : undefined,
    })
    return data
  },

  async getWeightProgress() {
    const { data } = await apiClient.get<WeightProgressPointResponse[]>('/stats/weight')
    return data
  },

  async saveWeightEntry(payload: WeightEntryRequest) {
    const { data } = await apiClient.post<WeightProgressPointResponse>('/stats/weight', payload)
    return data
  },

  async getStrengthProgress(exerciseId: string) {
    const { data } = await apiClient.get<StrengthProgressPointResponse[]>('/stats/progress', {
      params: { exerciseId },
    })
    return data
  },

  async getCalendar(month: number, year: number) {
    const { data } = await apiClient.get<CalendarWorkoutResponse[]>('/calendar', {
      params: { month, year },
    })
    return data
  },
}
