import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import '@testing-library/jest-dom';
import ProductDashboard from '../components/ProductDashboard';
import * as productService from '../services/productService';

// Mock Service API: Bắt buộc
jest.mock('../services/productService');

describe('Product Component Integration Tests (Form -> List)', () => {
    beforeEach(() => {
        // resetAllMocks sẽ xóa cả implementation queue của mockResolvedValueOnce giữa các test
        jest.resetAllMocks();
    });

    test('TC-INT-01: Tạo sản phẩm mới phải tự động cập nhật danh sách', async () => {
        // 1. SETUP MOCK CHO FETCH LẦN ĐẦU: Danh sách ban đầu trống
        productService.getProducts.mockResolvedValueOnce([]);

        // 2. SETUP MOCK CHO CREATE: Tạo thành công một sản phẩm (có id để tránh warning key và hỗ trợ edit/delete sau này)
        const newProduct = { id: 101, name: 'LapTop Asus', price: 999999, quantity: 10, category: 'LAPTOP', description: 'A powerful laptop' };
        productService.createProduct.mockResolvedValue(newProduct);

        // 3. SETUP MOCK CHO FETCH LẦN 2 (sau khi tạo): Trả về danh sách có sản phẩm mới
        // IMPORTANT: MockResolvedValueOnce thứ hai sẽ được dùng khi fetchProducts() chạy lại
        productService.getProducts.mockResolvedValueOnce([newProduct]);

        render(<ProductDashboard />);

        // Chờ đợi Dashboard tải xong (fetch lần 1)
        await waitFor(() => {
            expect(screen.queryByText(/Đang tải.../i)).not.toBeInTheDocument();
            // Khẳng định danh sách ban đầu trống (Sản phẩm Mới không có)
            expect(screen.queryByText(/LapTop Asus/i)).not.toBeInTheDocument();
        });

        // 4. Tương tác: Nhập và Submit Form
        fireEvent.change(screen.getByTestId('name-input'), { target: { value: 'LapTop Asus' } });
        // ... (Nhập các trường hợp lệ khác) ...
        fireEvent.click(screen.getByTestId('submit-product-btn'));

        // 5. Khẳng định: Component List đã được cập nhật
        await waitFor(() => {
            // Xác minh createProduct đã được gọi
            expect(productService.createProduct).toHaveBeenCalledTimes(1);
            // Xác minh List component hiển thị item mới
            expect(screen.getByText(/LapTop Asus/i)).toBeInTheDocument();
            // Khẳng định getProducts đã được gọi 2 lần (Initial load + Sau khi tạo)
            expect(productService.getProducts).toHaveBeenCalledTimes(2);
        });
    });

    test('TC-INT-02: Xóa sản phẩm phải gọi service và xóa khỏi danh sách', async () => {
        const existingProduct = { id: 55, name: 'LapTop Acer', price: 50000, quantity: 5, category: 'LAPTOP', description: 'An affordable laptop' };

        // 1. SETUP MOCK LẦN 1: Danh sách ban đầu có sản phẩm cũ
        productService.getProducts.mockResolvedValueOnce([existingProduct]);

        // 2. SETUP MOCK CHO DELETE: Service trả về thành công khi xóa
        productService.deleteProduct.mockResolvedValue({});

        // 3. SETUP MOCK LẦN 2: Sau khi xóa, danh sách trả về trống
        productService.getProducts.mockResolvedValueOnce([]);

        render(<ProductDashboard />);

        // Chờ đợi Dashboard tải xong
        await waitFor(() => {
            // Khẳng định sản phẩm ban đầu hiển thị
            expect(screen.getByText(/LapTop Acer/i)).toBeInTheDocument();
        });

        // 4. Tương tác: Nhấn nút Xóa
        // Giả định list item có nút xóa với role="button" và tên là "Xóa"
        const deleteButton = screen.getByRole('button', { name: /Xóa/i });
        fireEvent.click(deleteButton);

        // 5. Khẳng định: Component List đã được cập nhật
        await waitFor(() => {
            // Xác minh deleteProduct đã được gọi đúng ID
            expect(productService.deleteProduct).toHaveBeenCalledWith(existingProduct.id);

            // Xác minh sản phẩm không còn hiển thị (List đã tự fetch lại)
            expect(screen.queryByText(/LapTop Acer/i)).not.toBeInTheDocument();

            // Khẳng định getProducts đã được gọi 2 lần (Initial load + Sau khi xóa)
            expect(productService.getProducts).toHaveBeenCalledTimes(2);
        });
    });

    test('TC-INT-03: Chỉnh sửa sản phẩm phải cập nhật ProductList', async () => {
        const originalProduct = { id: 20, name: 'Bàn Phím Cơ', price: 1000000, quantity: 15, category: 'ACCESSORY', description: 'A mechanical keyboard' };
        const updatedName = 'Bàn Phím Cơ (Đã Sale)';

        // 1. SETUP MOCK LẦN 1: Danh sách ban đầu có sản phẩm gốc
        productService.getProducts.mockResolvedValueOnce([originalProduct]);

        // 2. SETUP MOCK CHO UPDATE: Service trả về thành công khi cập nhật
        productService.updateProduct.mockResolvedValue({
            id: 20,
            name: updatedName, price: 1000000, quantity: 15, category: 'ACCESSORY', description: 'A mechanical keyboard' // Dữ liệu mới
        });

        // 3. SETUP MOCK LẦN 2: Sau khi update, danh sách trả về dữ liệu mới
        productService.getProducts.mockResolvedValueOnce([{ id: 20, name: updatedName, price: 1000000, quantity: 15, category: 'ACCESSORY', description: 'A mechanical keyboard' }]);
        render(<ProductDashboard />);

        // Chờ đợi List component tải xong và hiển thị tên gốc
        await waitFor(() => {
            expect(screen.getByText(/Bàn Phím Cơ/i)).toBeInTheDocument();
            // Khẳng định tên MỚI chưa tồn tại
            expect(screen.queryByText(updatedName)).not.toBeInTheDocument();
        });

        // 4. Tương tác: Nhấn nút Edit trên sản phẩm
        // Giả định nút edit có data-testid="edit-20"
        const editButton = screen.getByTestId('edit-20');
        fireEvent.click(editButton);

        // 5. Khẳng định: Form chuyển sang chế độ Edit (có trường Name gốc)
        const nameInput = screen.getByTestId('name-input');
        await waitFor(() => {
            expect(nameInput).toHaveValue(originalProduct.name);
        });

        // 6. Tương tác: Nhập tên mới và Submit
        fireEvent.change(nameInput, { target: { value: updatedName } });
        fireEvent.click(screen.getByTestId('submit-product-btn'));

        // 7. Khẳng định: Service được gọi và List cập nhật
        await waitFor(() => {
            // Xác minh updateProduct đã được gọi
            expect(productService.updateProduct).toHaveBeenCalledTimes(1);
            // Xác minh tên mới hiển thị trên danh sách
            expect(screen.getByText(updatedName)).toBeInTheDocument();
            // Khẳng định tên cũ không còn hiển thị
            expect(screen.queryByText('Bàn Phím Cơ')).not.toBeInTheDocument();
        });
    });
});