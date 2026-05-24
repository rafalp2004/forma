import { useState } from 'react';
import { ProductSearch } from '../components/ProductSearch';

export const NutritionPage = () => {
    // Wyświetlanie daty
    const [selectedDate, setSelectedDate] = useState(new Date());

    const changeDate = (daysOffset: number) => {
        const newDate = new Date(selectedDate);
        newDate.setDate(newDate.getDate() + daysOffset);
        setSelectedDate(newDate);
    };

    const formatPolishDate = (date: Date, options: Intl.DateTimeFormatOptions) => {
        const formatted = new Intl.DateTimeFormat('pl-PL', options).format(date);
        return formatted.charAt(0).toUpperCase() + formatted.slice(1);
    };

    const mainDateText = formatPolishDate(selectedDate, {
        weekday: 'long', day: 'numeric', month: 'long', year: 'numeric'
    });

    const prevDay = new Date(selectedDate);
    prevDay.setDate(prevDay.getDate() - 1);
    const prevDayName = formatPolishDate(prevDay, { weekday: 'long' });

    const nextDay = new Date(selectedDate);
    nextDay.setDate(nextDay.getDate() + 1);
    const nextDayName = formatPolishDate(nextDay, { weekday: 'long' });


    return (
        <div className="p-6 max-w-[1600px] mx-auto">
            {/* Nagłówek */}
            <div className="flex justify-between items-center mb-8">
                <div>
                    <h1 className="text-3xl font-bold text-gray-900">Dziennik Diety</h1>
                    {/* Główna data */}
                    <p className="text-gray-500 capitalize">{mainDateText}</p>
                </div>
                <div className="flex gap-2 text-sm font-medium">
                    <button
                        onClick={() => changeDate(-1)}
                        className="text-primary hover:underline capitalize"
                    >
                        &lt; {prevDayName}
                    </button>

                    <span className="text-gray-300">|</span>

                    <button
                        onClick={() => changeDate(1)}
                        className="text-primary hover:underline capitalize"
                    >
                        {nextDayName} &gt;
                    </button>
                </div>
            </div>

            <div className="grid grid-cols-12 gap-6 items-start">

                <div className="col-span-12 lg:col-span-8 xl:col-span-9 space-y-6">
                    <div className="bg-gray-100 h-40 rounded-xl flex items-center justify-center border-2 border-dashed border-gray-300">
                        <p className="text-gray-400">Miejsce na Twoje Podsumowanie i Posiłki</p>
                    </div>
                </div>

                <div className="col-span-12 lg:col-span-4 xl:col-span-3 sticky top-6">
                    <ProductSearch />
                </div>

            </div>
        </div>
    );
};