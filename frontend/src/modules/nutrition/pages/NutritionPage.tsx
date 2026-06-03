import { useState, useEffect } from 'react';
import { ProductSearch } from '../components/ProductSearch';
import { MacroSummary } from '../components/MacroSummary';
import { ProductDto } from '@/shared/types';
import { apiClient } from '@/shared/api/client';

export interface MealEntryDto {
    id?: number;
    consumptionDate: string;
    mealType: string;
    productName: string;
    externalProductId: string;
    weightInGrams: number;
    calculatedCalories: number;
    calculatedProtein: number;
    calculatedCarbohydrates: number;
    calculatedFat: number;
}

export const NutritionPage = () => {
    const [selectedDate, setSelectedDate] = useState(new Date());
    const [meals, setMeals] = useState<MealEntryDto[]>([]);
    const [targets, setTargets] = useState({ kcal: 2000, protein: 100, fat: 60, carbs: 200 });

    const getFormattedDate = (date: Date) => {
        return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`;
    };

    const consumed = meals.reduce((acc, meal) => ({
        kcal: acc.kcal + meal.calculatedCalories,
        protein: acc.protein + meal.calculatedProtein,
        fat: acc.fat + meal.calculatedFat,
        carbs: acc.carbs + meal.calculatedCarbohydrates
    }), { kcal: 0, protein: 0, fat: 0, carbs: 0 });

    // Pobieranie targetów z backendu
    useEffect(() => {
        const fetchTargets = async () => {
            try {
                const response = await apiClient.get('/nutrition/targets');
                setTargets(response.data);
            } catch (error) {
                console.error("Błąd podczas pobierania celów makro:", error);
            }
        };
        fetchTargets();
    }, []);

    // Pobieranie posiłków z backendu
    useEffect(() => {
        const fetchMeals = async () => {
            try {
                const dateString = getFormattedDate(selectedDate);
                const response = await apiClient.get(`/nutrition/meals?date=${dateString}`);
                setMeals(response.data);
            } catch (error) {
                console.error("Błąd podczas pobierania posiłków:", error);
            }
        };
        fetchMeals();
    }, [selectedDate]);

    const handleAddProduct = async (product: ProductDto, grams: number) => {
        const multiplier = grams / 100;

        const newMeal = {
            consumptionDate: getFormattedDate(selectedDate),
            mealType: "SNACK",
            productName: product.name,
            externalProductId: product.id,
            weightInGrams: grams,
            calculatedCalories: product.kcalPer100g * multiplier,
            calculatedProtein: product.proteinPer100g * multiplier,
            calculatedFat: product.fatPer100g * multiplier,
            calculatedCarbohydrates: product.carbsPer100g * multiplier
        };

        try {
            const res = await apiClient.post('/nutrition/meals', newMeal);
            setMeals(prev => [...prev, res.data]);
        } catch (error) {
            console.error("Błąd dodawania posiłku:", error);
        }
    };

    const handleDeleteMeal = async (id: number) => {
        try {
            await apiClient.delete(`/nutrition/meals/${id}`);
            setMeals(prev => prev.filter(m => m.id !== id));
        } catch (error) {
            console.error("Błąd usuwania posiłku:", error);
        }
    };

    const handleSaveManualTargets = async (newTargets: { kcal: number, protein: number, fat: number, carbs: number }) => {
        try {
            const response = await apiClient.put('/nutrition/targets', newTargets);
            setTargets(response.data);
        } catch (error) {
            console.error("Błąd podczas zapisywania własnych celów:", error);
        }
    };

    const handleResetAutoTargets = async () => {
        try {
            const response = await apiClient.put('/nutrition/targets/reset');
            setTargets(response.data);
        } catch (error) {
            console.error("Błąd podczas przywracania celów auto:", error);
        }
    };

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

                {/* Lewa kolumna - podsumowanie i posiłki */}
                <div className="col-span-12 lg:col-span-8 xl:col-span-9 space-y-6">

                    <MacroSummary
                        kcal={{ current: consumed.kcal, max: targets.kcal }}
                        protein={{ current: consumed.protein, max: targets.protein }}
                        fat={{ current: consumed.fat, max: targets.fat }}
                        carbs={{ current: consumed.carbs, max: targets.carbs }}
                        onSaveManual={handleSaveManualTargets}
                        onResetAuto={handleResetAutoTargets}
                    />

                    {/* Lista posiłków */}
                    <div className="bg-white rounded-xl border border-gray-200 p-6 shadow-sm min-h-[300px]">
                        <h3 className="text-lg font-bold text-gray-800 mb-4 border-b pb-2">Zjedzone posiłki</h3>

                        {meals.length === 0 ? (
                            <div className="flex flex-col items-center justify-center h-40 text-gray-400">
                                <i className="fa-solid fa-plate-wheat text-4xl mb-2 opacity-50"></i>
                                <p className="text-sm">Nic jeszcze dzisiaj nie dodano.</p>
                            </div>
                        ) : (
                            <ul className="space-y-3">
                                {meals.map((meal) => (
                                    <li key={meal.id} className="flex justify-between items-center p-4 bg-gray-50 hover:bg-gray-100 rounded-lg border border-gray-100 transition-colors group">
                                        <div>
                                            <p className="font-bold text-gray-800 capitalize">{meal.productName}</p>
                                            <p className="text-xs text-gray-500 mt-1">
                                                <span className="font-semibold text-primary">{meal.weightInGrams}g</span>
                                                <span className="mx-2 text-gray-300">|</span>
                                                {Math.round(meal.calculatedCalories)} kcal
                                                <span className="text-gray-400 ml-1">
                                                    (B: {Math.round(meal.calculatedProtein)}g,
                                                    T: {Math.round(meal.calculatedFat)}g,
                                                    W: {Math.round(meal.calculatedCarbohydrates)}g)
                                                </span>
                                            </p>
                                        </div>
                                        <button
                                            onClick={() => meal.id && handleDeleteMeal(meal.id)}
                                            className="text-gray-300 hover:text-red-500 p-2 rounded-full hover:bg-red-50 transition-all opacity-0 group-hover:opacity-100"
                                            title="Usuń z dziennika"
                                        >
                                            <i className="fa-solid fa-trash"></i>
                                        </button>
                                    </li>
                                ))}
                            </ul>
                        )}
                    </div>

                </div>

                <div className="col-span-12 lg:col-span-4 xl:col-span-3 sticky top-6">
                    <ProductSearch onAddProduct={handleAddProduct} />
                </div>

            </div>
        </div>
    );
};