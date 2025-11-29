export const VALID_CATEGORIES = [
    'LAPTOP',
    'DESKTOP',
    'SMARTPHONE',
    'TABLET',
    'WEARABLE',
    'MONITOR',
    'PRINTER',
    'ACCESSORY',
    'NETWORK_DEVICE'
];

export function validateProduct(product = {}) {
    const errors = {};
    const { quantity, name, price, category, description } = product;
    const MAX_QUANTITY = 99999;
    const MIN_NAME_LENGTH = 3;
    const MAX_NAME_LENGTH = 100;
    const MAX_PRICE = 999999999;
    const MAX_DESCRIPTION_LENGTH = 500;

    // ================ Logic 1: Quantity Validation (GREEN cho Min/Max) ================

    if (quantity === undefined || quantity === null || !Number.isInteger(quantity)) {
        errors.quantity = 'Số lượng phải là một số nguyên hợp lệ';
    }
    else if (typeof quantity === 'number') {
        if (quantity < 0) {
            errors.quantity = 'Số lượng sản phẩm không được nhỏ hơn 0';
        } else if (quantity > MAX_QUANTITY) {
            errors.quantity = 'Số lượng sản phẩm không được vượt quá 99,999';
        }
    }

    // ================ Logic 2: Name Validation (GREEN cho Required) ================
    if (!name || name.trim().length === 0) {
        errors.name = 'Tên sản phẩm không được để trống';
    }
    else if (name.trim().length < MIN_NAME_LENGTH) {
        errors.name = 'Tên sản phẩm phải có ít nhất 3 ký tự';
    }
    else if (name.length > MAX_NAME_LENGTH) {
        errors.name = 'Tên sản phẩm không được vượt quá 100 ký tự';
    }

    // ================ Logic 3: Price Validation (RED cho Min) ================
    if (typeof price === 'number') {

        if (price < 0) {
            errors.price = 'Giá sản phẩm không được nhỏ hơn 0';
        }
        else if (price > MAX_PRICE) {
            errors.price = 'Giá sản phẩm không được vượt quá 999,999,999';
        }
    }

    // ================ Logic 4: Category Validation ================
    if (category === undefined || category === null || category.trim().length === 0
        || !VALID_CATEGORIES.includes(category)) {
        errors.category = 'Danh mục sản phẩm không hợp lệ';
    }

    // ================ Logic 5: Description Validation (optional field) ================
    if (typeof description === 'string') {
        const trimmedDesc = description.trim();
        if (trimmedDesc.length > MAX_DESCRIPTION_LENGTH) {
            errors.description = 'Mô tả sản phẩm không được vượt quá 500 ký tự';
        }
    }

    return errors;
}