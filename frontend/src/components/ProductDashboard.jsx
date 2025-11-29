// frontend/src/components/ProductDashboard.jsx
import React, { useState, useEffect, useMemo } from 'react';
import ProductList from './ProductList';
import ProductForm from './ProductForm';
import { getProducts, deleteProduct } from '../services/productService.js'; // named exports
import { VALID_CATEGORIES } from '../utils/productValidation.js';
import './ProductUI.css';

const ProductDashboard = () => {
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [editingProduct, setEditingProduct] = useState(null);
    // Search & Filters
    const [search, setSearch] = useState('');
    const [priceMin, setPriceMin] = useState('');
    const [priceMax, setPriceMax] = useState('');
    const [qtyMin, setQtyMin] = useState('');
    const [qtyMax, setQtyMax] = useState('');
    const [cat, setCat] = useState('');

    // Pagination statesconst [page, setPage] = useState(0);
    const [page, setPage] = useState(0);
    const [size] = useState(10);
    const [totalPages, setTotalPages] = useState(0);



    // Hàm fetch dữ liệu
    const fetchProducts = async () => {
        try {
            setLoading(true);

            const filters = {
                search,
                priceMin,
                priceMax,
                qtyMin,
                qtyMax,
                category: cat
            };

            const resp = await getProducts({ page, size, filters });

            // unwrap PagedResponse
            const paged = resp.data;
            const items = paged.items || paged.content || [];

            setProducts(items);
            setTotalPages(paged.totalPages ?? 0);

        } catch (err) {
            console.error("Error fetching products:", err);
        } finally {
            setLoading(false);
        }
    };


    // Hàm xử lý sau khi tạo/cập nhật thành công
    const handleProductActionSuccess = () => {
        // Sau khi tạo hoặc cập nhật thành công, tự động fetch lại danh sách để cập nhật
        fetchProducts();
        setEditingProduct(null); // Reset về chế độ tạo mới
    };
    const handleEditProduct = (product) => {
        setEditingProduct(product);
    };


    // Hàm xóa sản phẩm
    const handleDeleteProduct = async (id) => {
        try {
            await deleteProduct(id);
            // Sau khi xóa thành công, fetch lại danh sách
            fetchProducts();
        } catch (error) {
            console.error('Error deleting product:', error);
        }
    };

    const handleSearch = () => {
        setPage(0);     // reset to first page  
        fetchProducts();
    };

    useEffect(() => {
        fetchProducts();
    }, [page]);


    const clearFilters = () => {
        setSearch('');
        setPriceMin('');
        setPriceMax('');
        setQtyMin('');
        setQtyMax('');
        setCat('');
        setPage(0);     // reset when clearing filters
    };


    // const filteredProducts = useMemo(() => {
    //     const term = (search || '').toLowerCase();
    //     return (products || []).filter((p) => {
    //         const nameOk = term ? (p?.name || '').toLowerCase().includes(term) : true;
    //         const price = Number(p?.price ?? 0);
    //         const qty = Number(p?.quantity ?? 0);
    //         const priceMinOk = priceMin !== '' ? price >= Number(priceMin) : true;
    //         const priceMaxOk = priceMax !== '' ? price <= Number(priceMax) : true;
    //         const qtyMinOk = qtyMin !== '' ? qty >= Number(qtyMin) : true;
    //         const qtyMaxOk = qtyMax !== '' ? qty <= Number(qtyMax) : true;
    //         const catOk = cat ? p?.category === cat : true;
    //         return nameOk && priceMinOk && priceMaxOk && qtyMinOk && qtyMaxOk && catOk;
    //     });
    // }, [products, search, priceMin, priceMax, qtyMin, qtyMax, cat]);

    // Giả định truyền hàm callback xuống ProductForm
    return (
        <div className="page">
            <div className="page-header">
                <h2>Quản lý sản phẩm</h2>
            </div>

            <div className="layout">
                <aside className="filters card">
                    <div className="card-header">Tìm kiếm & Bộ lọc</div>
                    <div className="filters-body">
                        <div className="form-control">
                            <label>Tìm theo tên</label>
                            <input
                                data-testid="search-input"
                                value={search}
                                onChange={(e) => setSearch(e.target.value)}
                                placeholder="Nhập tên sản phẩm..."
                            />
                        </div>

                        <div className="filters-row">
                            <div className="form-control">
                                <label>Giá min</label>
                                <input type="number" min="0" value={priceMin} onChange={(e) => setPriceMin(e.target.value)} placeholder="0" />
                            </div>
                            <div className="form-control">
                                <label>Giá max</label>
                                <input type="number" min="0" value={priceMax} onChange={(e) => setPriceMax(e.target.value)} placeholder="999999" />
                            </div>
                        </div>

                        <div className="filters-row">
                            <div className="form-control">
                                <label>Số lượng min</label>
                                <input type="number" min="0" value={qtyMin} onChange={(e) => setQtyMin(e.target.value)} placeholder="0" />
                            </div>
                            <div className="form-control">
                                <label>Số lượng max</label>
                                <input type="number" min="0" value={qtyMax} onChange={(e) => setQtyMax(e.target.value)} placeholder="99999" />
                            </div>
                        </div>

                        <div className="form-control">
                            <label>Category</label>
                            <select value={cat} onChange={(e) => setCat(e.target.value)}>
                                <option value="">Tất cả</option>
                                {VALID_CATEGORIES.map((c) => (
                                    <option key={c} value={c}>{c}</option>
                                ))}
                            </select>
                        </div>

                        <div className="filters-actions">
                            <button type="button" className="btn" onClick={clearFilters}>
                                Làm mới bộ lọc
                            </button>

                            <button
                                type="button"
                                className="btn btn-primary"
                                data-testid="apply-filters-btn"
                                onClick={handleSearch}
                            >
                                Tìm kiếm
                            </button>
                        </div>

                    </div>
                </aside>

                <main className="content">
                    <ProductForm onActionSuccess={handleProductActionSuccess} editingProduct={editingProduct} />
                    {loading ? (
                        <div className="card"><div className="card-body">Đang tải...</div></div>
                    ) : (
                        <ProductList
                            products={products}
                            onDelete={handleDeleteProduct}
                            onEdit={handleEditProduct}
                        />
                    )}
                    <div className="pagination">
                        <button
                            data-testid="pagination-prev"
                            disabled={page === 0}
                            onClick={() => setPage(page - 1)}
                            className="btn"
                        >
                            Previous
                        </button>

                        <span data-testid="pagination-status" style={{ margin: "0 1rem" }}>
                            Page {page + 1} of {totalPages}
                        </span>

                        <button
                            data-testid="pagination-next"
                            disabled={page >= totalPages - 1}
                            onClick={() => setPage(page + 1)}
                            className="btn"
                        >
                            Next
                        </button>
                    </div>
                </main>

            </div>
        </div>
    );
};

export default ProductDashboard;