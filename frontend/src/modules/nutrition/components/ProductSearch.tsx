import { useState } from 'react';
import { Button, Input, Card } from '@/shared/components';
import { apiClient } from '@/shared/api/client';
import { ProductDto } from '@/shared/types';

interface ProductSearchProps {
    onAddProduct?: (product: ProductDto, grams: number) => void;
}

export const ProductSearch = ({ onAddProduct }: ProductSearchProps) => {
    const [query, setQuery] = useState('');
    const [products, setProducts] = useState<ProductDto[]>([]);
    const [isLoading, setIsLoading] = useState(false);

    const [selectedProduct, setSelectedProduct] = useState<ProductDto | null>(null);
    const [grams, setGrams] = useState<string>('100');

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

    const handleAddClick = (product: ProductDto) => {
        setSelectedProduct(product);
        setGrams('100'); // Domyślna porcja to 100g
    };

    const confirmAdd = () => {
        if (onAddProduct && selectedProduct) {
            onAddProduct(selectedProduct, Number(grams));
        }
        setSelectedProduct(null);
        setQuery('');
        setProducts([]);
    };

    // @ts-ignore
    return (
        <Card className="h-full flex flex-col relative overflow-hidden">
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
                            onClick={() => handleAddClick(product)}
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

            {selectedProduct && (
                <div className="absolute inset-0 bg-white/95 backdrop-blur-sm z-10 flex flex-col items-center justify-center p-6 border-t border-gray-100">
                    <div className="text-4xl mb-4">⚖️</div>
                    <h3 className="font-bold text-gray-800 text-lg mb-1 text-center line-clamp-2">
                        {selectedProduct.name}
                    </h3>
                    <p className="text-sm text-gray-500 mb-6">Podaj wagę zjedzonej porcji</p>

                    <div className="flex items-center gap-2 mb-6 w-full max-w-[200px]">
                        <Input
                            type="number"
                            min="1"
                            value={grams}
                            onChange={(e) => setGrams(e.target.value)}
                            className="text-center text-lg font-bold"
                            autoFocus
                            onKeyDown={(e) => e.key === 'Enter' && confirmAdd()}
                        />
                        <span className="text-gray-500 font-medium">gramów</span>
                    </div>

                    <div className="flex gap-3 w-full">
                        <Button variant="outline" className="flex-1" onClick={() => setSelectedProduct(null)}>
                            Anuluj
                        </Button>
                        <Button className="flex-1" onClick={confirmAdd}>
                            Dodaj
                        </Button>
                    </div>
                </div>
            )}
        </Card>
    );
};