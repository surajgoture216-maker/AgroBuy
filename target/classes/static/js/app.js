// JavaScript for Fertilizer Shop

document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function(tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Add to cart functionality
    console.log('Setting up add-to-cart listeners...');
    document.querySelectorAll('.add-to-cart').forEach(function(button) {
        console.log('Found add-to-cart button:', button);
        button.addEventListener('click', function() {
            const productId = this.getAttribute('data-product-id');
            console.log('Add to cart clicked for product:', productId);
            addToCart(productId);
        });
    });

    // Quantity update functionality
    document.querySelectorAll('.quantity-input').forEach(function(input) {
        input.addEventListener('change', function() {
            const productId = this.getAttribute('data-product-id');
            const quantity = this.value;
            updateQuantity(productId, quantity);
        });
    });

    // Quantity increase/decrease buttons
    document.querySelectorAll('.quantity-increase').forEach(function(button) {
        button.addEventListener('click', function() {
            const productId = this.getAttribute('data-product-id');
            const input = document.querySelector('.quantity-input[data-product-id="' + productId + '"]');
            if (input) {
                const newQuantity = parseInt(input.value) + 1;
                input.value = newQuantity;
                updateQuantity(productId, newQuantity);
            }
        });
    });

    document.querySelectorAll('.quantity-decrease').forEach(function(button) {
        button.addEventListener('click', function() {
            const productId = this.getAttribute('data-product-id');
            const input = document.querySelector('.quantity-input[data-product-id="' + productId + '"]');
            if (input) {
                const newQuantity = Math.max(1, parseInt(input.value) - 1);
                input.value = newQuantity;
                updateQuantity(productId, newQuantity);
            }
        });
    });

    // Delete item functionality
    document.querySelectorAll('.delete-item').forEach(function(button) {
        button.addEventListener('click', function() {
            const productId = this.getAttribute('data-product-id');
            deleteItem(productId);
        });
    });

    // Form validation
    const forms = document.querySelectorAll('.needs-validation');
    forms.forEach(function(form) {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        });
    });

    // Auto-hide alerts after 5 seconds
    setTimeout(function() {
        const alerts = document.querySelectorAll('.alert');
        alerts.forEach(function(alert) {
            if (alert.classList.contains('show')) {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            }
        });
    }, 5000);

    // Confirm delete actions
    document.querySelectorAll('[data-confirm-delete]').forEach(function(button) {
        button.addEventListener('click', function(event) {
            if (!confirm('Are you sure you want to delete this item? This action cannot be undone.')) {
                event.preventDefault();
            }
        });
    });

    // Loading states for forms
    document.querySelectorAll('form').forEach(function(form) {
        form.addEventListener('submit', function() {
            const submitButton = form.querySelector('button[type="submit"]');
            if (submitButton) {
                submitButton.disabled = true;
                submitButton.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Processing...';
            }
        });
    });
});

// Add to cart function
function addToCart(productId, quantity = 1) {
    console.log('Making addToCart request for product:', productId, 'quantity:', quantity);
    const headers = {
        'Content-Type': 'application/x-www-form-urlencoded',
        'X-Requested-With': 'XMLHttpRequest'
    };

    if (typeof csrfHeader !== 'undefined' && typeof csrfToken !== 'undefined') {
        headers[csrfHeader] = csrfToken;
        console.log('Adding CSRF header:', csrfHeader, csrfToken);
    }

    fetch('/cart/add', {
        method: 'POST',
        headers: headers,
        body: new URLSearchParams({
            'productId': productId,
            'quantity': quantity
        })
    })
    .then(response => {
        console.log('Response status:', response.status);
        return response.json();
    })
    .then(data => {
        console.log('Response data:', data);
        if (data.success) {
            showToast('success', 'Product added to cart successfully!');
            updateCartCount(data.cartCount);
        } else {
            showToast('error', data.message || 'Failed to add product to cart');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showToast('error', 'An error occurred. Please try again.');
    });
}

// Update quantity function
function updateQuantity(productId, quantity) {
    const headers = {
        'Content-Type': 'application/x-www-form-urlencoded',
        'X-Requested-With': 'XMLHttpRequest'
    };
    
    if (typeof csrfHeader !== 'undefined' && typeof csrfToken !== 'undefined') {
        headers[csrfHeader] = csrfToken;
    }
    
    fetch('/cart/update', {
        method: 'POST',
        headers: headers,
        body: new URLSearchParams({
            'productId': productId,
            'quantity': quantity
        })
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            location.reload();
        } else {
            showToast('error', data.message || 'Failed to update quantity');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        showToast('error', 'An error occurred. Please try again.');
    });
}

// Delete item function
function deleteItem(productId) {
    if (confirm('Are you sure you want to remove this item from your cart?')) {
        fetch(`/cart/remove/${productId}`, {
            method: 'GET',
            headers: {
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                location.reload();
            } else {
                showToast('error', data.message || 'Failed to remove item');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showToast('error', 'An error occurred. Please try again.');
        });
    }
}

// Show toast notification
function showToast(type, message) {
    const toastContainer = document.getElementById('toast-container');
    if (!toastContainer) return;

    const toastId = 'toast-' + Date.now();
    const toastType = type === 'success' ? 'text-bg-success' : 'text-bg-danger';
    const icon = type === 'success' ? 'check-circle' : 'exclamation-triangle';

    const toastHtml = `
        <div id="${toastId}" class="toast align-items-center ${toastType}" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body">
                    <i class="fas fa-${icon} me-2"></i>${message}
                </div>
                <button type="button" class="btn-close me-2" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>
    `;

    toastContainer.insertAdjacentHTML('beforeend', toastHtml);
    
    const toastElement = document.getElementById(toastId);
    const toast = new bootstrap.Toast(toastElement);
    toast.show();

    // Remove toast element after it's hidden
    toastElement.addEventListener('hidden.bs.toast', function() {
        this.remove();
    });
}

// Update cart count in navbar
function updateCartCount(count) {
    const cartBadge = document.querySelector('.navbar .fa-shopping-cart + .badge');
    if (cartBadge) {
        cartBadge.textContent = count;
    }
}

// Format currency
function formatCurrency(amount) {
    return new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency: 'INR',
        minimumFractionDigits: 2
    }).format(amount);
}

// Debounce function for search
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

// Search functionality with debounce
const debouncedSearch = debounce(function(searchTerm) {
    if (searchTerm.length >= 2) {
        // Perform search
        window.location.href = `/products?search=${encodeURIComponent(searchTerm)}`;
    } else if (searchTerm.length === 0) {
        window.location.href = '/products';
    }
}, 300);

// Search input event listener
const searchInput = document.getElementById('searchInput');
if (searchInput) {
    searchInput.addEventListener('input', function() {
        debouncedSearch(this.value);
    });
}

// Order status color coding
document.addEventListener('DOMContentLoaded', function() {
    const statusElements = document.querySelectorAll('[data-status]');
    statusElements.forEach(function(element) {
        const status = element.getAttribute('data-status');
        element.classList.add(`status-${status.toLowerCase()}`);
    });
});
