import { HTMLAttributes } from 'react'

interface CardProps extends HTMLAttributes<HTMLDivElement> {
  title?: string
}

export function Card({ title, children, className = '', ...props }: CardProps) {
  return (
    <div
      {...props}
      className={`rounded-xl border border-gray-200 bg-white p-5 shadow-sm ${className}`}
    >
      {title && <h2 className="mb-4 text-base font-semibold text-gray-900">{title}</h2>}
      {children}
    </div>
  )
}
