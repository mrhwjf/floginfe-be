import '@testing-library/jest-dom';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
// Giả định bạn có component ProductForm và ProductList
import ProductForm from '../components/ProductForm'; 
import ProductList from '../components/ProductList'; 
import * as productService from '../services/productService'; 

// BƯỚC QUAN TRỌNG: Mock toàn bộ module productService
jest.mock('../services/productService');

// Thiết lập dọn dẹp mock giữa các test
beforeEach(() => {
    jest.clearAllMocks();
});

describe('Product Mock Testing (Câu 4.2.1)', () => {
    test('TC-MOCK-01: Tao san pham thanh cong (Success Flow)', async () => {
        // 1. Setup Mock: Ép hàm createProduct() trả về thành công
        productService.createProduct.mockResolvedValue({ 
            message: 'Tạo sản phẩm thành công', 
            data: { id: 101, name: 'Mock Laptop' } 
        });

        render(<ProductForm />);
        
        // 2. Tương tác (Giả định form có input và nút submit)
        fireEvent.change(screen.getByTestId('name-input'), { target: { value: 'Laptop ABC' } });
        fireEvent.click(screen.getByTestId('submit-product-btn')); 

        // 3. Khẳng định: Service được gọi và Component xử lý thành công
        await waitFor(() => {
            // Xác minh mock đã được gọi (0.5 điểm)
            expect(productService.createProduct).toHaveBeenCalledTimes(1); 
            // Khẳng định thông báo thành công hiển thị
            expect(screen.getByText(/Tạo sản phẩm thành công/i)).toBeInTheDocument(); 
        });
    });

    test('TC-MOCK-02: Tao san pham that bai (Failure Flow)', async () => {
        // 1. Setup Mock: Ép hàm createProduct() trả về lỗi
        productService.createProduct.mockRejectedValue(new Error('Lỗi: Tên sản phẩm đã tồn tại'));

        render(<ProductForm />);
        
        fireEvent.change(screen.getByTestId('name-input'), { target: { value: 'Laptop cũ' } });
        fireEvent.click(screen.getByTestId('submit-product-btn')); 

        // 2. Khẳng định: Component hiển thị lỗi từ server
        await waitFor(() => {
            expect(screen.getByText(/Lỗi: Tên sản phẩm đã tồn tại/i)).toBeInTheDocument();
            // Xác minh mock đã được gọi (0.5 điểm)
            expect(productService.createProduct).toHaveBeenCalledTimes(1); 
        });
    });

    test('TC-MOCK-03: Hien thi danh sach san pham khi fetch thanh cong', async () => {
        const mockProducts = [
            { id: 1, name: 'Phone', price: 1000 },
            { id: 2, name: 'Mouse', price: 50 }
        ];

        // 1. Setup Mock: Ép getProducts trả về danh sách giả
        productService.getProducts.mockResolvedValue(mockProducts);

        render(<ProductList />);
        
        // 2. Khẳng định: Service đã được gọi
        expect(productService.getProducts).toHaveBeenCalledTimes(1);

        // 3. Khẳng định: Component hiển thị dữ liệu giả
        await waitFor(() => {
            expect(screen.getByText(/Phone/i)).toBeInTheDocument();
            expect(screen.getByText(/Mouse/i)).toBeInTheDocument();
            // Kiểm tra số lượng item hiển thị
            // Giả định list item có data-testid="product-item"
            expect(screen.getAllByTestId('product-item')).toHaveLength(2); 
        });
    });
});