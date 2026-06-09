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

export const planningApi = {
  async getPlans() {
    const { data } = await apiClient.get<unknown>('/plans')
    return asArray<TrainingPlanResponse>(data).map(normalizePlan)
  },

  async createPlan(payload: TrainingPlanRequest) {
    const { data } = await apiClient.post<TrainingPlanResponse>('/plans', payload)
    return normalizePlan(data)
  },

  async getPlan(id: number) {
    const { data } = await apiClient.get<TrainingPlanResponse>(`/plans/${id}`)
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
