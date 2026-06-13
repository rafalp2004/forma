import { createBrowserRouter, Navigate } from 'react-router-dom'
import { Layout } from '@/shared/components'
import { useAuthStore } from '@/shared/store/auth.store'

// Auth
import { LoginPage } from '@/modules/auth/pages/LoginPage'
import { RegisterPage } from '@/modules/auth/pages/RegisterPage'

// App
import { DashboardPage } from '@/modules/dashboard/pages/DashboardPage'
import { WorkoutsPage } from '@/modules/workout/pages/WorkoutsPage'
import { ExercisesPage } from '@/modules/workout/pages/ExercisesPage'
import { PlansPage } from '@/modules/planning/pages/PlansPage'
import { StatsPage } from '@/modules/planning/pages/StatsPage'
import { CalendarPage } from '@/modules/planning/pages/CalendarPage'
import { FriendsPage } from '@/modules/social/pages/FriendsPage'
import { ChallengesPage } from '@/modules/social/pages/ChallengesPage'
import { NutritionPage } from '@/modules/nutrition/pages/NutritionPage'
import { ProfilePage } from '@/modules/profile/pages/ProfilePage'

// eslint-disable-next-line react-refresh/only-export-components
function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated)()
  return isAuthenticated ? <>{children}</> : <Navigate to="/login" replace />
}

export const router = createBrowserRouter([
  // Publiczne
  { path: '/login', element: <LoginPage /> },
  { path: '/register', element: <RegisterPage /> },

  // Chronione – wszystkie mają wspólny Layout z Sidebarem
  {
    element: (
      <ProtectedRoute>
        <Layout />
      </ProtectedRoute>
    ),
    children: [
      { path: '/', element: <Navigate to="/dashboard" replace /> },
      { path: '/dashboard', element: <DashboardPage /> },

      // Mateusz
      { path: '/workouts', element: <WorkoutsPage /> },
      { path: '/exercises', element: <ExercisesPage /> },

      // Antoni
      { path: '/plans', element: <PlansPage /> },
      { path: '/stats', element: <StatsPage /> },
      { path: '/calendar', element: <CalendarPage /> },

      // Rafał
      { path: '/social/friends', element: <FriendsPage /> },
      { path: '/social/challenges', element: <ChallengesPage /> },

      // Oskar
      { path: '/nutrition', element: <NutritionPage /> },

      // Wspólny
      { path: '/profile', element: <ProfilePage /> },
    ],
  },
])
