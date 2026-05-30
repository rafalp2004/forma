import { FormEvent, useEffect, useMemo, useState } from 'react'
import { Button, Card, Input } from '@/shared/components'
import { planningApi } from '../api'
import type {
  ExerciseDto,
  PlanExerciseRequest,
  TrainingPlanRequest,
  TrainingPlanResponse,
} from '../types'

const dayNames = ['Pon', 'Wt', 'Sr', 'Czw', 'Pt', 'Sob', 'Nd']

const emptyPlanForm: TrainingPlanRequest = {
  name: '',
  description: '',
  startDate: '',
  endDate: '',
  exercises: [],
}

function toRequest(plan: TrainingPlanResponse): TrainingPlanRequest {
  return {
    name: plan.name,
    description: plan.description ?? '',
    startDate: plan.startDate ?? '',
    endDate: plan.endDate ?? '',
    exercises: plan.exercises.map(({ exerciseId, exerciseName, dayOfWeek, sets, reps, targetWeightKg }) => ({
      exerciseId,
      exerciseName,
      dayOfWeek,
      sets,
      reps,
      targetWeightKg,
    })),
  }
}

function normalizeForm(form: TrainingPlanRequest): TrainingPlanRequest {
  return {
    ...form,
    description: form.description?.trim() ? form.description.trim() : null,
    startDate: form.startDate || null,
    endDate: form.endDate || null,
  }
}

export function PlansPage() {
  const [plans, setPlans] = useState<TrainingPlanResponse[]>([])
  const [selectedPlanId, setSelectedPlanId] = useState<number | null>(null)
  const [form, setForm] = useState<TrainingPlanRequest>(emptyPlanForm)
  const [exerciseDraft, setExerciseDraft] = useState<PlanExerciseRequest>({
    exerciseId: '',
    exerciseName: '',
    dayOfWeek: 1,
    sets: 3,
    reps: 10,
    targetWeightKg: null,
  })
  const [exerciseSearch, setExerciseSearch] = useState('')
  const [exerciseOptions, setExerciseOptions] = useState<ExerciseDto[]>([])
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')

  const selectedPlan = useMemo(
    () => plans.find((plan) => plan.id === selectedPlanId) ?? null,
    [plans, selectedPlanId]
  )

  const loadPlans = async () => {
    setError('')
    setLoading(true)
    try {
      const data = await planningApi.getPlans()
      setPlans(data)
      if (!selectedPlanId && data[0]) {
        setSelectedPlanId(data[0].id)
        setForm(toRequest(data[0]))
      }
    } catch {
      setError('Nie udalo sie pobrac planow.')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    void loadPlans()
  }, [])

  useEffect(() => {
    const timeoutId = window.setTimeout(async () => {
      try {
        const data = await planningApi.getExercises(exerciseSearch.trim() || undefined)
        setExerciseOptions(data.slice(0, 8))
      } catch {
        setExerciseOptions([])
      }
    }, 250)

    return () => window.clearTimeout(timeoutId)
  }, [exerciseSearch])

  const startNewPlan = () => {
    setSelectedPlanId(null)
    setForm(emptyPlanForm)
    setError('')
  }

  const selectPlan = (plan: TrainingPlanResponse) => {
    setSelectedPlanId(plan.id)
    setForm(toRequest(plan))
    setError('')
  }

  const savePlan = async (event: FormEvent) => {
    event.preventDefault()
    setError('')

    if (!form.name.trim()) {
      setError('Nazwa planu jest wymagana.')
      return
    }

    setSaving(true)
    try {
      const payload = normalizeForm({ ...form, name: form.name.trim() })
      const saved = selectedPlanId
        ? await planningApi.updatePlan(selectedPlanId, payload)
        : await planningApi.createPlan(payload)

      setPlans((current) => {
        const exists = current.some((plan) => plan.id === saved.id)
        return exists ? current.map((plan) => (plan.id === saved.id ? saved : plan)) : [saved, ...current]
      })
      setSelectedPlanId(saved.id)
      setForm(toRequest(saved))
    } catch {
      setError('Nie udalo sie zapisac planu. Sprawdz daty i pola cwiczen.')
    } finally {
      setSaving(false)
    }
  }

  const deleteSelectedPlan = async () => {
    if (!selectedPlanId) {
      return
    }

    setSaving(true)
    setError('')
    try {
      await planningApi.deletePlan(selectedPlanId)
      const nextPlans = plans.filter((plan) => plan.id !== selectedPlanId)
      setPlans(nextPlans)
      if (nextPlans[0]) {
        setSelectedPlanId(nextPlans[0].id)
        setForm(toRequest(nextPlans[0]))
      } else {
        startNewPlan()
      }
    } catch {
      setError('Nie udalo sie usunac planu.')
    } finally {
      setSaving(false)
    }
  }

  const addExercise = () => {
    if (!exerciseDraft.exerciseId || !exerciseDraft.exerciseName) {
      setError('Wybierz cwiczenie z listy albo wpisz jego ID i nazwe.')
      return
    }

    setForm((current) => ({
      ...current,
      exercises: [...current.exercises, exerciseDraft],
    }))
    setExerciseDraft({
      exerciseId: '',
      exerciseName: '',
      dayOfWeek: 1,
      sets: 3,
      reps: 10,
      targetWeightKg: null,
    })
    setExerciseSearch('')
    setError('')
  }

  const removeExercise = (index: number) => {
    setForm((current) => ({
      ...current,
      exercises: current.exercises.filter((_, currentIndex) => currentIndex !== index),
    }))
  }

  return (
    <div>
      <div className="mb-6 flex flex-col gap-3 sm:flex-row sm:items-start sm:justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Plany treningowe</h1>
          <p className="mt-1 text-sm text-gray-500">Tworz i edytuj swoje plany na kolejne tygodnie.</p>
        </div>
        <Button type="button" onClick={startNewPlan}>
          Nowy plan
        </Button>
      </div>

      {error && (
        <div className="mb-4 rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {error}
        </div>
      )}

      <div className="grid grid-cols-1 gap-5 xl:grid-cols-[360px_1fr]">
        <Card title="Twoje plany">
          {loading ? (
            <p className="text-sm text-gray-500">Ladowanie planow...</p>
          ) : plans.length === 0 ? (
            <p className="text-sm text-gray-500">Nie masz jeszcze zadnego planu.</p>
          ) : (
            <div className="space-y-2">
              {plans.map((plan) => (
                <button
                  key={plan.id}
                  type="button"
                  onClick={() => selectPlan(plan)}
                  className={`w-full rounded-lg border p-3 text-left transition-colors ${
                    selectedPlanId === plan.id
                      ? 'border-primary bg-primary-light'
                      : 'border-gray-200 bg-white hover:bg-gray-50'
                  }`}
                >
                  <div className="flex items-center justify-between gap-3">
                    <span className="truncate text-sm font-semibold text-gray-900">{plan.name}</span>
                    <span className="rounded-full bg-gray-100 px-2 py-0.5 text-xs text-gray-600">
                      {plan.status}
                    </span>
                  </div>
                  <p className="mt-1 text-xs text-gray-500">
                    {plan.exercises.length} cwiczen
                    {plan.startDate ? ` od ${plan.startDate}` : ''}
                  </p>
                </button>
              ))}
            </div>
          )}
        </Card>

        <Card title={selectedPlan ? `Edycja: ${selectedPlan.name}` : 'Nowy plan'}>
          <form onSubmit={savePlan} className="space-y-5">
            <div className="grid grid-cols-1 gap-4 md:grid-cols-2">
              <Input
                label="Nazwa"
                value={form.name}
                onChange={(event) => setForm((current) => ({ ...current, name: event.target.value }))}
                placeholder="Plan silowy"
                required
              />
              <div className="grid grid-cols-2 gap-3">
                <Input
                  label="Start"
                  type="date"
                  value={form.startDate ?? ''}
                  onChange={(event) => setForm((current) => ({ ...current, startDate: event.target.value }))}
                />
                <Input
                  label="Koniec"
                  type="date"
                  value={form.endDate ?? ''}
                  onChange={(event) => setForm((current) => ({ ...current, endDate: event.target.value }))}
                />
              </div>
            </div>

            <label className="flex flex-col gap-1">
              <span className="text-sm font-medium text-gray-700">Opis</span>
              <textarea
                value={form.description ?? ''}
                onChange={(event) => setForm((current) => ({ ...current, description: event.target.value }))}
                className="min-h-20 rounded-lg border border-gray-300 px-3 py-2 text-sm outline-none transition-colors placeholder:text-gray-400 focus:border-primary focus:ring-2 focus:ring-primary/20"
                placeholder="Cel planu, zalozenia i uwagi"
              />
            </label>

            <div className="rounded-lg border border-gray-200 p-4">
              <h3 className="mb-3 text-sm font-semibold text-gray-900">Dodaj cwiczenie</h3>
              <div className="grid grid-cols-1 gap-3 lg:grid-cols-[1fr_90px_90px_90px_120px_auto]">
                <div>
                  <Input
                    label="Cwiczenie"
                    value={exerciseSearch}
                    onChange={(event) => {
                      setExerciseSearch(event.target.value)
                      setExerciseDraft((current) => ({
                        ...current,
                        exerciseName: event.target.value,
                        exerciseId: current.exerciseId || event.target.value,
                      }))
                    }}
                    placeholder="Szukaj cwiczenia"
                  />
                  {exerciseOptions.length > 0 && (
                    <div className="mt-1 max-h-40 overflow-y-auto rounded-lg border border-gray-200 bg-white shadow-sm">
                      {exerciseOptions.map((exercise) => (
                        <button
                          key={exercise.id}
                          type="button"
                          onClick={() => {
                            setExerciseDraft((current) => ({
                              ...current,
                              exerciseId: exercise.id,
                              exerciseName: exercise.name,
                            }))
                            setExerciseSearch(exercise.name)
                            setExerciseOptions([])
                          }}
                          className="block w-full px-3 py-2 text-left text-sm hover:bg-gray-50"
                        >
                          <span className="font-medium text-gray-900">{exercise.name}</span>
                          <span className="ml-2 text-xs text-gray-500">{exercise.muscleGroup}</span>
                        </button>
                      ))}
                    </div>
                  )}
                </div>
                <label className="flex flex-col gap-1">
                  <span className="text-sm font-medium text-gray-700">Dzien</span>
                  <select
                    value={exerciseDraft.dayOfWeek}
                    onChange={(event) =>
                      setExerciseDraft((current) => ({ ...current, dayOfWeek: Number(event.target.value) }))
                    }
                    className="rounded-lg border border-gray-300 px-3 py-2 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-primary/20"
                  >
                    {dayNames.map((day, index) => (
                      <option key={day} value={index + 1}>
                        {day}
                      </option>
                    ))}
                  </select>
                </label>
                <Input
                  label="Serie"
                  type="number"
                  min={1}
                  value={exerciseDraft.sets}
                  onChange={(event) =>
                    setExerciseDraft((current) => ({ ...current, sets: Number(event.target.value) }))
                  }
                />
                <Input
                  label="Powt."
                  type="number"
                  min={1}
                  value={exerciseDraft.reps}
                  onChange={(event) =>
                    setExerciseDraft((current) => ({ ...current, reps: Number(event.target.value) }))
                  }
                />
                <Input
                  label="Ciezar kg"
                  type="number"
                  min={0}
                  step="0.5"
                  value={exerciseDraft.targetWeightKg ?? ''}
                  onChange={(event) =>
                    setExerciseDraft((current) => ({
                      ...current,
                      targetWeightKg: event.target.value ? Number(event.target.value) : null,
                    }))
                  }
                />
                <div className="flex items-end">
                  <Button type="button" variant="secondary" onClick={addExercise} className="w-full">
                    Dodaj
                  </Button>
                </div>
              </div>
            </div>

            <div className="overflow-hidden rounded-lg border border-gray-200">
              <table className="min-w-full divide-y divide-gray-200 text-sm">
                <thead className="bg-gray-50 text-left text-xs font-semibold uppercase text-gray-500">
                  <tr>
                    <th className="px-3 py-2">Cwiczenie</th>
                    <th className="px-3 py-2">Dzien</th>
                    <th className="px-3 py-2">Serie</th>
                    <th className="px-3 py-2">Powt.</th>
                    <th className="px-3 py-2">Ciezar</th>
                    <th className="px-3 py-2" />
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-100 bg-white">
                  {form.exercises.length === 0 ? (
                    <tr>
                      <td colSpan={6} className="px-3 py-6 text-center text-gray-500">
                        Dodaj pierwsze cwiczenie do planu.
                      </td>
                    </tr>
                  ) : (
                    form.exercises.map((exercise, index) => (
                      <tr key={`${exercise.exerciseId}-${index}`}>
                        <td className="px-3 py-2 font-medium text-gray-900">{exercise.exerciseName}</td>
                        <td className="px-3 py-2 text-gray-600">{dayNames[exercise.dayOfWeek - 1]}</td>
                        <td className="px-3 py-2 text-gray-600">{exercise.sets}</td>
                        <td className="px-3 py-2 text-gray-600">{exercise.reps}</td>
                        <td className="px-3 py-2 text-gray-600">
                          {exercise.targetWeightKg ? `${exercise.targetWeightKg} kg` : '-'}
                        </td>
                        <td className="px-3 py-2 text-right">
                          <Button type="button" variant="ghost" onClick={() => removeExercise(index)}>
                            Usun
                          </Button>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>

            <div className="flex flex-col-reverse gap-3 sm:flex-row sm:justify-between">
              <Button
                type="button"
                variant="ghost"
                onClick={deleteSelectedPlan}
                disabled={!selectedPlanId || saving}
              >
                Usun plan
              </Button>
              <Button type="submit" loading={saving}>
                Zapisz plan
              </Button>
            </div>
          </form>
        </Card>
      </div>
    </div>
  )
}
