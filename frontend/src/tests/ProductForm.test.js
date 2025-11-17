// frontend/src/tests/ProductForm.test.js

import { render, screen, fireEvent } from '@testing-library/react';
import '@testing-library/jest-dom';
import ProductForm from '../components/ProductForm'; // Giả định file component nằm ở đây

// Import hàm validation (sẽ mock)
import { validateProduct } from '../utils/productValidation'; 

// MOCK HÀM VALIDATEPRODUCT: Chúng ta cần kiểm soát kết quả validation
jest.mock('../utils/productValidation', () => ({
  validateProduct: jest.fn(),
}));

// Định nghĩa một test case thất bại (RED Phase)
describe('ProductForm Component Validation Handling', () => {
  
  test('TC-RED-10: Nên hiển thị thông báo lỗi khi validateProduct trả về lỗi', async () => {
    
    // 1. MOCK: Thiết lập validateProduct trả về lỗi Name rỗng
    validateProduct.mockReturnValue({
        name: 'Tên sản phẩm không được để trống'
    });

    // Giả định form có các input sau (Cần thêm data-testid vào component)
    render(<ProductForm />);

    // 2. Giao diện: Tìm nút Submit (Cần data-testid="submit-product-btn")
    const submitButton = screen.getByRole('button', { name: /Lưu/i });
    
    // 3. Tương tác: Nhấn nút Submit
    fireEvent.click(submitButton);

    // 4. Khẳng định: Kiểm tra rằng hàm validate đã được gọi
    expect(validateProduct).toHaveBeenCalled();

    // 5. Khẳng định: Kiểm tra form hiển thị lỗi validation
    // Giả định component sử dụng data-testid="name-error"
    const nameError = await screen.findByTestId('name-error');
    expect(nameError).toHaveTextContent('Tên sản phẩm không được để trống');
  });
  
  // Bạn có thể thêm test case để kiểm tra form submit thành công (khi validation pass)
});