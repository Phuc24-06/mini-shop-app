# ✅ Thêm Navigation Cho Category Ở HomeScreen

## 🎯 Yêu Cầu
Khi click vào category ở **HomeScreen** → Navigate đến **ProductsByCategoryScreen** hiển thị sản phẩm của danh mục đó (giống CategoryScreen)

## 🔧 Thay Đổi

### 1. HomeScreen.kt - Thêm onCategoryClick Parameter
```kotlin
// TRƯỚC:
@Composable
fun HomeScreen(
    onProductClick: (Product) -> Unit = {}
) { ... }

// SAU:
@Composable
fun HomeScreen(
    onProductClick: (Product) -> Unit = {},
    onCategoryClick: (categoryId: String, categoryName: String) -> Unit = { _, _ -> }
) { ... }
```

### 2. HomeScreen.kt - Truyền onClick vào CategoryItemView
```kotlin
// TRƯỚC:
categories.forEach { item ->
    CategoryItemView(name = item.name, iconUrl = item.imageUrl)
    // ❌ Không có onClick
}

// SAU:
categories.forEach { item ->
    CategoryItemView(
        name = item.name,
        iconUrl = item.imageUrl,
        onClick = {
            onCategoryClick(item.id, item.name)  // ✅ Navigate khi click
        }
    )
}
```

### 3. ShopBottomNav.kt - Implement onCategoryClick
```kotlin
HomeScreen(
    onProductClick = { product -> ... },
    onCategoryClick = { categoryId, categoryName ->
        val encodedId = java.net.URLEncoder.encode(categoryId, "UTF-8")
        val encodedName = java.net.URLEncoder.encode(categoryName, "UTF-8")
        navController.navigate("category_products/$encodedId?name=$encodedName")
    }
)
```

## ✨ Kết Quả

### Luồng Navigation:

```
HomeScreen
   ↓ (click category "Electronics")
CategoryItemView onClick
   ↓
onCategoryClick("cat123", "Electronics")
   ↓
navController.navigate("category_products/cat123?name=Electronics")
   ↓
ProductsByCategoryScreen
   ↓
Hiển thị danh sách sản phẩm thuộc category "Electronics"
```

### Hành Vi:

| Action | Kết quả |
|--------|---------|
| Click "Electronics" ở Home | → ProductsByCategoryScreen với sản phẩm Electronics ✅ |
| Click "Gadgets" ở Home | → ProductsByCategoryScreen với sản phẩm Gadgets ✅ |
| Click "Fashion" ở Home | → ProductsByCategoryScreen với sản phẩm Fashion ✅ |
| Click sản phẩm | → ProductDetailScreen ✅ |

## 📋 Tính Năng Tương Tự

Bây giờ **HomeScreen** và **CategoryScreen** đều có thể navigate đến ProductsByCategoryScreen:

- ✅ **HomeScreen** → Click category → ProductsByCategoryScreen
- ✅ **CategoryScreen** → Click category → ProductsByCategoryScreen
- ✅ Cùng logic, cùng UI, cùng trải nghiệm

## 📁 Files Đã Sửa

1. ✅ `HomeScreen.kt` - Thêm `onCategoryClick` parameter và truyền vào `CategoryItemView`
2. ✅ `ShopBottomNav.kt` - Implement callback để navigate

## 🚀 Test

1. Mở app → Vào Home
2. Scroll danh mục ngang ở phía trên
3. Click vào bất kỳ category nào (VD: "Electronics")
4. **Kết quả:** Navigate đến ProductsByCategoryScreen hiển thị sản phẩm Electronics ✅
5. Click sản phẩm → Xem chi tiết ✅
6. Back → Về ProductsByCategoryScreen
7. Back → Về Home ✅

---

**Ngày sửa:** 2025-11-20  
**Trạng thái:** ✅ Hoàn thành  
**Lợi ích:** Nhất quán navigation, UX tốt hơn!

