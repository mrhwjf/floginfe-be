import { validateProduct } from '../utils/productValidation.js';

describe('validateProduct - Quantity rules', () => {
    
    //================= Quantity Validation Tests =================

    // TDD Cycle 1 - Sửa lại RED Phase: Kiểm tra thuộc tính 'quantity' trong đối tượng errors
    test('TC-GREEN-01 :should return error when quantity is negative', () => {
        const product = { quantity: -5 };
        const errors = validateProduct(product);
        
        // Khẳng định rằng thuộc tính 'quantity' phải tồn tại và có giá trị lỗi mong muốn.
        expect(errors.quantity).toBe('Số lượng sản phẩm không được nhỏ hơn 0');
    });

    // TDD Cycle 2 - RED: Quantity vượt quá giới hạn (100000)
    test('TC-GREEN-02: should return error when quantity exceeds maximum (99,999)', () => {
        const product = { quantity: 100000 };
        const errors = validateProduct(product);
        expect(errors.quantity).toBe('Số lượng sản phẩm không được vượt quá 99,999');
    });

    //=================== Name Validation Tests ===================

    // --- TDD Cycle 3: Product Name Required (GREEN) ---
    test('TC-GREEN-03: should return error when Product Name is empty', () => {
        const product = { name: '', quantity: 10 }; 
        const errors = validateProduct(product);
        
        // Khẳng định rằng lỗi Name Required phải tồn tại
        expect(errors.name).toBe('Tên sản phẩm không được để trống');
    });

    // --- TDD Cycle 4: Product Name Min Length (RED Phase) ---
    test('TC-RED-04: should return error when Product Name is too short (less than 3 chars)', () => {
        const product = { name: 'AB', quantity: 10 }; // 2 ký tự
        const errors = validateProduct(product);
        
        // Khẳng định rằng lỗi Name Min Length phải tồn tại
        expect(errors.name).toBe('Tên sản phẩm phải có ít nhất 3 ký tự');
    });

    test('TC-RED-05: should return error when Product Name is too long (more than 100 chars)', () => {
        // Tạo chuỗi 101 ký tự ('A' lặp lại 101 lần)
        const longName = 'A'.repeat(101);
        const product = { name: longName, quantity: 10, price: 100 }; 
        const errors = validateProduct(product);
        
        // Khẳng định rằng lỗi Name Max Length phải tồn tại
        expect(errors.name).toBe('Tên sản phẩm không được vượt quá 100 ký tự');
    });


    // =============== Price Validation Tests ===============

    test('TC-RED-06: Should return error when Price is negative', () => {
        // Dữ liệu chỉ có price âm, các trường khác hợp lệ
        const product = { name: 'Valid Name', quantity: 10, price: -100 }; 
        const errors = validateProduct(product);
        
        // Khẳng định rằng lỗi Price Min phải tồn tại
        expect(errors.price).toBe('Giá sản phẩm không được nhỏ hơn 0');
    });

    test('TC-RED-07: Should return error when Price exceeds maximum (999,999,999)', () => {
        // Giá trị không hợp lệ: 1 tỷ
        const product = { name: 'Valid Name', quantity: 10, price: 1000000000 }; 
        const errors = validateProduct(product);
        
        // Khẳng định rằng lỗi Price Max phải tồn tại
        expect(errors.price).toBe('Giá sản phẩm không được vượt quá 999,999,999');
    });

    // ============== Category Validation Tests ===============
    test('TC-RED-08: Should return error when Category is not in the valid list', () => {
        // Dữ liệu category không hợp lệ
        const product = { 
            name: 'Valid Product', 
            quantity: 10, 
            price: 100,
            category: 'Food' // Category không hợp lệ
        }; 
        const errors = validateProduct(product);
        
        // Khẳng định rằng lỗi Category phải tồn tại
        expect(errors.category).toBe('Danh mục sản phẩm không hợp lệ');
    });

    // ============== Description Validation Tests ===============
    test('TC-RED-09: Should return error when Description is too long (more than 500 chars)', () => {
        // Tạo chuỗi 501 ký tự ('X' lặp lại 501 lần)
        const longDescription = 'X'.repeat(501); 
        const product = { 
            name: 'Valid Product', 
            quantity: 10, 
            price: 100,
            description: longDescription // Quá 500 ký tự
        }; 
        const errors = validateProduct(product);
        
        // Khẳng định rằng lỗi Description Max Length phải tồn tại
        expect(errors.description).toBe('Mô tả sản phẩm không được vượt quá 500 ký tự');
    });

    // ============== Data Type Validation Tests ===============
    test('TC-RED-10: Should return error when Quantity is not a valid integer', () => {
        // Input Quantity là chuỗi ký tự
        const product = { name: 'Valid Name', quantity: 'ABC', price: 100 }; 
        const errors = validateProduct(product);
        
        // Khẳng định rằng lỗi kiểu dữ liệu phải tồn tại
        expect(errors.quantity).toBe('Số lượng phải là một số nguyên hợp lệ');
    });

});