import { NavLink, useNavigate } from 'react-router-dom'
import { useAuthStore } from '../store/auth.store'

interface NavItem {
  label: string
  to: string
  icon: string
}

const navItems: NavItem[] = [
  { label: 'Dashboard',   to: '/dashboard',           icon: '⊞' },
  { label: 'Treningi',    to: '/workouts',             icon: '🏋' },
  { label: 'Ćwiczenia',  to: '/exercises',            icon: '📋' },
  { label: 'Plany',       to: '/plans',                icon: '📅' },
  { label: 'Postęp',      to: '/stats',                icon: '📈' },
  { label: 'Kalendarz',   to: '/calendar',             icon: '🗓' },
  { label: 'Znajomi',     to: '/social/friends',       icon: '👥' },
  { label: 'Wyzwania',    to: '/social/challenges',    icon: '🏆' },
  { label: 'Dieta',       to: '/nutrition',            icon: '🥗' },
]

export function Sidebar() {
  const navigate = useNavigate()
  const { user, logout } = useAuthStore()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <aside className="flex h-screen w-sidebar flex-col bg-sidebar text-gray-100">
      {/* Logo */}
      <div className="flex h-16 items-center px-6">
        <span className="text-xl font-bold text-primary">FORMA</span>
      </div>

      {/* Navigation */}
      <nav className="flex-1 overflow-y-auto px-3 py-2">
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            className={({ isActive }) =>
              `flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-colors mb-0.5
              ${isActive
                ? 'bg-primary text-white'
                : 'text-gray-400 hover:bg-white/10 hover:text-white'
              }`
            }
          >
            <span className="text-base">{item.icon}</span>
            {item.label}
          </NavLink>
        ))}
      </nav>

      {/* User + logout */}
      <div className="border-t border-white/10 p-4">
        <NavLink
          to="/profile"
          className={({ isActive }) =>
            `flex items-center gap-3 rounded-lg px-3 py-2 text-sm transition-colors
            ${isActive ? 'bg-primary text-white' : 'text-gray-400 hover:text-white'}`
          }
        >
          <span className="text-base">👤</span>
          <span className="flex-1 truncate">{user?.username ?? 'Profil'}</span>
        </NavLink>
        <button
          onClick={handleLogout}
          className="mt-1 flex w-full items-center gap-3 rounded-lg px-3 py-2 text-sm text-gray-400 transition-colors hover:bg-white/10 hover:text-white"
        >
          <span className="text-base">↩</span>
          Wyloguj
        </button>
      </div>
    </aside>
  )
}
