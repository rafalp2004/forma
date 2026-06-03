import { useState } from 'react';
import { Card, Button, Input } from '@/shared/components';

interface MacroSummaryProps {
    kcal: { current: number; max: number };
    protein: { current: number; max: number };
    fat: { current: number; max: number };
    carbs: { current: number; max: number };
    onSaveManual?: (targets: { kcal: number, protein: number, fat: number, carbs: number }) => void;
    onResetAuto?: () => void;
}

export const MacroSummary = ({ kcal, protein, fat, carbs, onSaveManual, onResetAuto }: MacroSummaryProps) => {
    const [isEditing, setIsEditing] = useState(false);
    const [editData, setEditData] = useState({ kcal: 0, protein: 0, fat: 0, carbs: 0 });

    const calcPercent = (current: number, max: number) => {
        if (max === 0) return 0;
        return Math.min((current / max) * 100, 100);
    };

    const handleOpenEdit = () => {
        setEditData({ kcal: kcal.max, protein: protein.max, fat: fat.max, carbs: carbs.max });
        setIsEditing(true);
    };

    const handleSave = () => {
        if (onSaveManual) onSaveManual(editData);
        setIsEditing(false);
    };

    const handleReset = () => {
        if (onResetAuto) onResetAuto();
        setIsEditing(false);
    };

    return (
        <Card className="w-full relative overflow-hidden transition-all duration-300">

            {!isEditing ? (
                <>
                    <div className="flex justify-between items-start mb-6">
                        <h2 className="text-xl font-bold text-gray-800">Podsumowanie dnia</h2>
                        <button
                            onClick={handleOpenEdit}
                            className="text-gray-400 hover:text-primary transition-colors flex items-center gap-2 text-sm font-medium"
                        >
                            <i className="fa-solid fa-gear"></i> Edytuj cele
                        </button>
                    </div>

                    {/* Główny pasek - kalorie */}
                    <div className="mb-8">
                        <div className="flex justify-between items-end mb-2">
                            <span className="font-semibold text-gray-700 text-lg">Kalorie</span>
                            <span className="text-gray-500 font-medium">
                                <span className="text-gray-900 text-xl font-bold">{Math.round(kcal.current)}</span> / {kcal.max} kcal
                            </span>
                        </div>
                        <div className="w-full bg-gray-100 rounded-full h-4 border border-gray-200 overflow-hidden">
                            <div className="bg-orange-400 h-full rounded-full transition-all duration-1000 ease-out" style={{ width: `${calcPercent(kcal.current, kcal.max)}%` }}></div>
                        </div>
                    </div>

                    {/* Paski makroskładników */}
                    <div className="grid grid-cols-3 gap-6">
                        {/* Białko */}
                        <div>
                            <div className="flex justify-between items-end mb-1 text-sm">
                                <span className="font-medium text-gray-600">Białko</span>
                                <span className="text-gray-500">{Math.round(protein.current)} / {protein.max}g</span>
                            </div>
                            <div className="w-full bg-gray-100 rounded-full h-2 border border-gray-200 overflow-hidden">
                                <div className="bg-blue-400 h-full rounded-full transition-all duration-1000 ease-out delay-100" style={{ width: `${calcPercent(protein.current, protein.max)}%` }}></div>
                            </div>
                        </div>
                        {/* Tłuszcze */}
                        <div>
                            <div className="flex justify-between items-end mb-1 text-sm">
                                <span className="font-medium text-gray-600">Tłuszcze</span>
                                <span className="text-gray-500">{Math.round(fat.current)} / {fat.max}g</span>
                            </div>
                            <div className="w-full bg-gray-100 rounded-full h-2 border border-gray-200 overflow-hidden">
                                <div className="bg-yellow-400 h-full rounded-full transition-all duration-1000 ease-out delay-200" style={{ width: `${calcPercent(fat.current, fat.max)}%` }}></div>
                            </div>
                        </div>
                        {/* Węglowodany */}
                        <div>
                            <div className="flex justify-between items-end mb-1 text-sm">
                                <span className="font-medium text-gray-600">Węglowodany</span>
                                <span className="text-gray-500">{Math.round(carbs.current)} / {carbs.max}g</span>
                            </div>
                            <div className="w-full bg-gray-100 rounded-full h-2 border border-gray-200 overflow-hidden">
                                <div className="bg-green-400 h-full rounded-full transition-all duration-1000 ease-out delay-300" style={{ width: `${calcPercent(carbs.current, carbs.max)}%` }}></div>
                            </div>
                        </div>
                    </div>
                </>
            ) : (
                /* Widok edycji */
                <div className="flex flex-col justify-center py-2">
                    <h3 className="font-bold text-gray-800 text-lg mb-6 text-center">Spersonalizuj swoje cele</h3>

                    <div className="grid grid-cols-2 gap-6 mb-8">
                        <div>
                            <label className="text-xs font-bold text-gray-500 uppercase">Kalorie (kcal)</label>
                            <Input type="number" min="0" value={editData.kcal} onChange={(e) => setEditData({...editData, kcal: Number(e.target.value)})} className="mt-1 font-bold" />
                        </div>
                        <div>
                            <label className="text-xs font-bold text-gray-500 uppercase">Białko (g)</label>
                            <Input type="number" min="0" value={editData.protein} onChange={(e) => setEditData({...editData, protein: Number(e.target.value)})} className="mt-1 font-bold" />
                        </div>
                        <div>
                            <label className="text-xs font-bold text-gray-500 uppercase">Tłuszcze (g)</label>
                            <Input type="number" min="0" value={editData.fat} onChange={(e) => setEditData({...editData, fat: Number(e.target.value)})} className="mt-1 font-bold" />
                        </div>
                        <div>
                            <label className="text-xs font-bold text-gray-500 uppercase">Węglowodany (g)</label>
                            <Input type="number" min="0" value={editData.carbs} onChange={(e) => setEditData({...editData, carbs: Number(e.target.value)})} className="mt-1 font-bold" />
                        </div>
                    </div>

                    <div className="flex gap-3 mt-auto">
                        <Button variant="outline" className="flex-1 text-xs" onClick={handleReset}>
                            <i className="fa-solid fa-rotate-left mr-1"></i> Auto
                        </Button>
                        <Button variant="outline" className="flex-1 text-xs" onClick={() => setIsEditing(false)}>
                            Anuluj
                        </Button>
                        <Button className="flex-1 text-xs" onClick={handleSave}>
                            Zapisz
                        </Button>
                    </div>
                </div>
            )}
        </Card>
    );
};