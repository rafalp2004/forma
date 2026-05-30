import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuthStore } from '@/shared/store/auth.store'
import { Card, Button, Input } from '@/shared/components'
import { apiClient } from '@/shared/api/client'
import { UserDetailsResponse, Gender, UserGoal } from '@/shared/types'

export function ProfilePage() {
  const navigate = useNavigate()
  const logout = useAuthStore((s) => s.logout)
  const [profile, setProfile] = useState<UserDetailsResponse | null>(null)
  const [loading, setLoading] = useState(true)
  
  // Edit states
  const [isEditingBiometrics, setIsEditingBiometrics] = useState(false)
  const [isEditingGoals, setIsEditingGoals] = useState(false)
  const [editBioForm, setEditBioForm] = useState({ age: 0, weight: 0, height: 0, gender: Gender.MALE })
  const [editGoalForm, setEditGoalForm] = useState({ goal: UserGoal.MAINTAIN_WEIGHT })

  const fetchProfile = async () => {
    try {
      const { data } = await apiClient.get<UserDetailsResponse>('/users/me')
      setProfile(data)
      setEditBioForm({
        age: data.age || 0,
        weight: data.weight || 0,
        height: data.height || 0,
        gender: data.gender || Gender.MALE
      })
      setEditGoalForm({
        goal: data.goal || UserGoal.MAINTAIN_WEIGHT
      })
    } catch (error) {
      console.error('Failed to fetch profile:', error)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchProfile()
  }, [])

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const handleSaveBiometrics = async () => {
    try {
      await apiClient.put('/users/me/biometrics', editBioForm)
      setIsEditingBiometrics(false)
      fetchProfile()
    } catch (error) {
      console.error('Failed to update biometrics:', error)
    }
  }

  const handleSaveGoals = async () => {
    try {
      await apiClient.put('/users/me/goals', editGoalForm)
      setIsEditingGoals(false)
      fetchProfile()
    } catch (error) {
      console.error('Failed to update goals:', error)
    }
  }

  if (loading) {
    return <div className="p-8 text-gray-500 font-medium">Ładowanie profilu...</div>
  }

  const getInitials = (name: string) => {
    return name
      .split(' ')
      .map((n) => n[0])
      .join('')
      .toUpperCase()
  }

  const formatGoal = (goal: UserGoal | null) => {
    switch (goal) {
      case UserGoal.LOSE_WEIGHT: return 'Redukcja'
      case UserGoal.GAIN_WEIGHT: return 'Budowa masy mięśniowej'
      case UserGoal.MAINTAIN_WEIGHT: return 'Utrzymanie wagi'
      default: return 'Nie określono'
    }
  }

  const formatGender = (gender: Gender | null) => {
    switch (gender) {
      case Gender.MALE: return 'Mężczyzna'
      case Gender.FEMALE: return 'Kobieta'
      default: return 'Nie określono'
    }
  }

  const bmi = profile?.weight && profile?.height 
    ? (profile.weight / Math.pow(profile.height / 100, 2)).toFixed(1)
    : null

  const getBmiCategory = (bmiVal: string) => {
    const val = parseFloat(bmiVal)
    if (val < 18.5) return 'Niedowaga'
    if (val < 25) return 'Norma'
    if (val < 30) return 'Nadwaga'
    return 'Otyłość'
  }

  return (
    <div className="max-w-7xl mx-auto pb-12">
      <h1 className="text-3xl font-bold text-gray-900 mb-8">Mój Profil</h1>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
        {/* Left & Center Column */}
        <div className="lg:col-span-8 space-y-8">
          
          {/* Profile Header Card */}
          <div className="bg-white rounded-2xl p-8 border border-gray-100 flex flex-col md:flex-row items-center gap-8 shadow-sm relative">
            <div className="w-32 h-32 bg-primary rounded-full flex items-center justify-center text-white text-4xl font-bold">
              {profile ? getInitials(profile.username) : '??'}
            </div>
            <div className="flex-1 text-center md:text-left">
              <h2 className="text-3xl font-bold text-gray-900">{profile?.username || 'Użytkownik'}</h2>
              <p className="text-gray-500 font-medium">@{profile?.username?.toLowerCase().replace(' ', '_') || 'user'}</p>
              <div className="mt-4 flex flex-wrap justify-center md:justify-start gap-4 text-sm">
                <span className="bg-emerald-50 text-emerald-700 px-3 py-1 rounded-full border border-emerald-100 font-medium">
                  🏋️ Entuzjasta siłowni
                </span>
                <span className="text-gray-400">
                  📅 W FORMA od {profile?.createdAt ? new Date(profile.createdAt).toLocaleDateString('pl-PL', { month: 'long', year: 'numeric' }) : '---'}
                </span>
              </div>
              
              <div className="mt-8 flex gap-12 justify-center md:justify-start">
                <div className="text-center md:text-left">
                  <div className="text-2xl font-bold text-primary">{profile?.workoutCount ?? 0}</div>
                  <div className="text-xs text-gray-400 font-bold uppercase tracking-wider">Treningów</div>
                </div>
                <div className="text-center md:text-left">
                  <div className="text-2xl font-bold text-primary">12</div>
                  <div className="text-xs text-gray-400 font-bold uppercase tracking-wider">Przejechanych</div>
                </div>
                <div className="text-center md:text-left">
                  <div className="text-2xl font-bold text-primary">{profile?.challengeCount ?? 0}</div>
                  <div className="text-xs text-gray-400 font-bold uppercase tracking-wider">Wyzwań</div>
                </div>
              </div>
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            {/* Biometrics */}
            <Card className="p-0 border-gray-100 overflow-hidden">
              <div className="p-6 border-b border-gray-50 flex justify-between items-center">
                <h3 className="font-bold text-gray-900">Dane biometryczne</h3>
                {!isEditingBiometrics && (
                  <button 
                    onClick={() => setIsEditingBiometrics(true)}
                    className="text-xs text-primary hover:text-primary-hover font-bold uppercase tracking-widest"
                  >
                    Edytuj
                  </button>
                )}
              </div>
              <div className="p-6 space-y-4">
                {isEditingBiometrics ? (
                  <div className="space-y-4">
                    <Input 
                      label="Wiek" 
                      type="number" 
                      value={editBioForm.age} 
                      onChange={(e) => setEditBioForm({...editBioForm, age: parseInt(e.target.value)})}
                    />
                    <Input 
                      label="Wzrost (cm)" 
                      type="number" 
                      value={editBioForm.height} 
                      onChange={(e) => setEditBioForm({...editBioForm, height: parseFloat(e.target.value)})}
                    />
                    <Input 
                      label="Waga (kg)" 
                      type="number" 
                      value={editBioForm.weight} 
                      onChange={(e) => setEditBioForm({...editBioForm, weight: parseFloat(e.target.value)})}
                    />
                    <div className="flex flex-col gap-1">
                      <label className="text-sm font-medium text-gray-700">Płeć</label>
                      <select 
                        className="rounded-lg border border-gray-300 px-3 py-2 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-primary/20"
                        value={editBioForm.gender}
                        onChange={(e) => setEditBioForm({...editBioForm, gender: e.target.value as Gender})}
                      >
                        <option value={Gender.MALE}>Mężczyzna</option>
                        <option value={Gender.FEMALE}>Kobieta</option>
                      </select>
                    </div>
                    <div className="flex gap-2 pt-2">
                      <Button onClick={handleSaveBiometrics} className="flex-1">Zapisz</Button>
                      <Button variant="secondary" onClick={() => setIsEditingBiometrics(false)} className="flex-1">Anuluj</Button>
                    </div>
                  </div>
                ) : (
                  <>
                    <div className="flex justify-between items-center">
                      <span className="text-gray-400 text-sm">Wiek</span>
                      <span className="font-semibold text-gray-700">{profile?.age || '--'} lata</span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span className="text-gray-400 text-sm">Wzrost</span>
                      <span className="font-semibold text-gray-700">{profile?.height || '--'} cm</span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span className="text-gray-400 text-sm">Waga</span>
                      <span className="font-semibold text-gray-700">{profile?.weight || '--'} kg</span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span className="text-gray-400 text-sm">Płeć</span>
                      <span className="font-semibold text-gray-700">{formatGender(profile?.gender || null)}</span>
                    </div>
                    <div className="flex justify-between items-center">
                      <span className="text-gray-400 text-sm">BMI</span>
                      <span className="font-semibold text-gray-700">{bmi || '--'} ({bmi ? getBmiCategory(bmi) : '--'})</span>
                    </div>
                  </>
                )}
              </div>
            </Card>

            {/* Goals */}
            <Card className="p-0 border-gray-100 overflow-hidden">
              <div className="p-6 border-b border-gray-50 flex justify-between items-center">
                <h3 className="font-bold text-gray-900">Cele treningowe</h3>
                {!isEditingGoals && (
                  <button 
                    onClick={() => setIsEditingGoals(true)}
                    className="text-xs text-primary hover:text-primary-hover font-bold uppercase tracking-widest"
                  >
                    Edytuj
                  </button>
                )}
              </div>
              <div className="p-6 space-y-5">
                {isEditingGoals ? (
                  <div className="space-y-4">
                    <div className="flex flex-col gap-1">
                      <label className="text-sm font-medium text-gray-700">Cel główny</label>
                      <select 
                        className="rounded-lg border border-gray-300 px-3 py-2 text-sm outline-none focus:border-primary focus:ring-2 focus:ring-primary/20"
                        value={editGoalForm.goal}
                        onChange={(e) => setEditGoalForm({ goal: e.target.value as UserGoal })}
                      >
                        <option value={UserGoal.LOSE_WEIGHT}>Redukcja</option>
                        <option value={UserGoal.GAIN_WEIGHT}>Budowa masy mięśniowej</option>
                        <option value={UserGoal.MAINTAIN_WEIGHT}>Utrzymanie wagi</option>
                      </select>
                    </div>
                    <div className="flex gap-2 pt-2">
                      <Button onClick={handleSaveGoals} className="flex-1">Zapisz</Button>
                      <Button variant="secondary" onClick={() => setIsEditingGoals(false)} className="flex-1">Anuluj</Button>
                    </div>
                  </div>
                ) : (
                  <>
                    <div>
                      <div className="text-gray-400 text-xs uppercase tracking-widest mb-1">Cel główny</div>
                      <div className="font-bold text-gray-800">{formatGoal(profile?.goal || null)}</div>
                    </div>
                    <div>
                      <div className="text-gray-400 text-xs uppercase tracking-widest mb-1">Waga docelowa</div>
                      <div className="font-bold text-gray-800">85 kg</div>
                    </div>
                    <div>
                      <div className="text-gray-400 text-xs uppercase tracking-widest mb-1">Sesje / tydzień</div>
                      <div className="font-bold text-gray-800">5 sesji</div>
                    </div>
                  </>
                )}
              </div>
            </Card>
          </div>

          {/* Records */}
          <Card className="p-0 border-gray-100 overflow-hidden">
            <div className="p-6 border-b border-gray-50">
              <h3 className="font-bold text-gray-900">Rekordy osobiste (PR)</h3>
            </div>
            <div className="p-6 grid grid-cols-1 sm:grid-cols-3 gap-6">
              {profile?.personalRecords && profile.personalRecords.length > 0 ? (
                profile.personalRecords.map((record, i) => (
                  <div key={i} className="bg-emerald-50 rounded-xl p-4 border border-emerald-100">
                    <div className="text-primary text-xl font-black mb-1">{record.weight} kg</div>
                    <div className="text-gray-700 font-bold text-sm mb-1">{record.exerciseName}</div>
                    <div className="text-gray-400 text-[10px]">{new Date(record.date).toLocaleDateString('pl-PL')}</div>
                  </div>
                ))
              ) : (
                <div className="sm:col-span-3 text-center py-4 text-gray-400 text-sm">Brak rekordów do wyświetlenia</div>
              )}
            </div>
          </Card>
        </div>

        {/* Right Column - Settings */}
        <div className="lg:col-span-4 space-y-8">
          <Card className="p-0 border-gray-100 overflow-hidden">
            <div className="p-6 border-b border-gray-50">
              <h3 className="font-bold text-gray-900">Ustawienia konta</h3>
            </div>
            <div className="p-6 space-y-6">
              <div className="flex justify-between items-center">
                <div>
                  <div className="font-medium text-gray-800 text-sm">Powiadomienia</div>
                  <div className="text-xs text-primary font-medium">Włączone</div>
                </div>
                <div className="w-12 h-6 bg-primary rounded-full relative cursor-pointer">
                  <div className="absolute right-1 top-1 w-4 h-4 bg-white rounded-full shadow-sm" />
                </div>
              </div>

              <div className="space-y-1">
                <div className="font-medium text-gray-800 text-sm">Widoczność profilu</div>
                <div className="text-xs text-gray-400">Tylko znajomi</div>
              </div>

              <div className="pt-8 border-t border-gray-50">
                <Button 
                  variant="ghost" 
                  className="w-full text-red-500 hover:text-red-600 hover:bg-red-50 font-bold py-3"
                  onClick={handleLogout}
                >
                  Wyloguj się
                </Button>
              </div>
            </div>
          </Card>
        </div>
      </div>
    </div>
  )
}
