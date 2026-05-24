import { useState } from 'react';
import { Button, Input, Card } from '@/shared/components';
import { apiClient } from '@/shared/api/client';
import { ProductDto } from '@/shared/types';

export const ProductSearch = () => {
    const [query, setQuery] = useState('');
    const [products, setProducts] = useState<ProductDto[]>([]);
    const [isLoading, setIsLoading] = useState(false);

    const handleSearch = async () => {
        if (!query.trim()) return;
        setIsLoading(true);
        try {
            const response = await apiClient.get<ProductDto[]>('/nutrition/products', {
                params: { query }
            });
            setProducts(response.data);
        } catch (error) {
            console.error('Błąd wyszukiwania:', error);
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <Card className="h-full flex flex-col">
            <h2 className="text-lg font-bold mb-4">Wyszukaj produkt</h2>

            <div className="relative mb-6">
                <Input
                    value={query}
                    onChange={(e) => setQuery(e.target.value)}
                    placeholder="Szukaj produktu spożywczego..."
                    className="pr-10"
                    onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
                />
                <button
                    onClick={handleSearch}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-gray-400 hover:text-primary"
                >
                    <i className="fa-solid fa-magnifying-glass"></i>
                </button>
            </div>

            <div className="flex-1 overflow-y-auto space-y-4">
                {products.map((product) => (
                    <div key={product.externalId} className="flex items-center justify-between group p-2 rounded-lg hover:bg-gray-50 transition-colors">

                        <div className="flex flex-col gap-1 w-full pr-4">
                            <span className="font-bold text-gray-800 line-clamp-1" title={product.name}>
                                {product.name}
                            </span>

                            <div className="flex flex-wrap items-center gap-2 text-[11px] font-medium">
                                <span className="bg-gray-100 text-gray-600 px-2 py-0.5 rounded-md border border-gray-200">
                                    na 100g
                                </span>
                                <span className="bg-orange-50 text-orange-600 px-2 py-0.5 rounded-md border border-orange-100">
                                    Kalorie {Math.round(product.kcalPer100g)} kcal
                                </span>
                                <span className="bg-blue-50 text-blue-600 px-2 py-0.5 rounded-md border border-blue-100">
                                    Białko: {Math.round(product.proteinPer100g)}g
                                </span>
                                <span className="bg-yellow-50 text-yellow-600 px-2 py-0.5 rounded-md border border-yellow-100">
                                    Tłuszcze: {Math.round(product.fatPer100g)}g
                                </span>
                                <span className="bg-green-50 text-green-600 px-2 py-0.5 rounded-md border border-green-100">
                                    Węglowodany: {Math.round(product.carbsPer100g)}g
                                </span>
                            </div>
                        </div>

                        <Button
                            className="rounded-lg h-8 w-8 p-0 flex items-center justify-center bg-primary text-white shrink-0 hover:opacity-90 transition-opacity"
                        >
                            +
                        </Button>
                    </div>
                ))}

                {products.length === 0 && !isLoading && (
                    <p className="text-center text-gray-400 text-sm mt-10">
                        Zacznij pisać, aby wyszukać produkty...
                    </p>
                )}
            </div>
        </Card>
    );
};