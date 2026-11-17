const express = require('express');
const cors = require('cors');

const app = express();
app.use(express.json());
app.use(cors());

// Dữ liệu mẫu lưu tạm trong bộ nhớ RAM của Server
let contacts = [
  { id: 1, name: 'Trần Phát Tài', email: 'phattai@ut.edu.vn', balance: 500 },
  { id: 2, name: 'Nguyễn Văn A', email: 'vana@ut.edu.vn', balance: 200 }
];

// 1. GET: Lấy danh sách liên lạc
app.get('/api/contacts', (req, res) => {
  res.status(200).json(contacts);
});

// 2. POST: Thêm mới kèm Server-side Validation (Trang 127)
app.post('/api/contacts', (req, res) => {
  const { name, email } = req.body;

  if (!name || name.trim() === '') {
    return res.status(400).json({ error: 'Tên không được để trống (400 Bad Request)' });
  }
  if (name.length > 100) {
    return res.status(400).json({ error: 'Tên không được vượt quá 100 ký tự' });
  }
  if (!email || !email.includes('@')) {
    return res.status(400).json({ error: 'Email không hợp lệ' });
  }

  const newContact = {
    id: Date.now(),
    name: name.trim(),
    email: email.trim(),
    balance: 100
  };

  contacts.push(newContact);
  res.status(201).json(newContact);
});

// 3. DELETE: Xóa liên lạc
app.delete('/api/contacts/:id', (req, res) => {
  const id = Number(req.params.id);
  contacts = contacts.filter(c => c.id !== id);
  res.status(200).json({ success: true });
});

// 4. POST: Chuyển tiền nguyên tử - Atomic Transaction & Rollback (Trang 129)
app.post('/api/transfer', (req, res) => {
  const { fromId, toId, amount } = req.body;
  const transferAmount = Number(amount);

  if (!transferAmount || transferAmount <= 0) {
    return res.status(400).json({ error: 'Số tiền chuyển phải lớn hơn 0' });
  }

  // Lưu trạng thái trước giao dịch để Rollback nếu có lỗi
  const snapshot = JSON.parse(JSON.stringify(contacts));

  try {
    const source = contacts.find(c => c.id === Number(fromId));
    const destination = contacts.find(c => c.id === Number(toId));

    if (!source || !destination) {
      throw new Error('Không tìm thấy tài khoản nguồn hoặc đích');
    }

    // Trừ tiền tài khoản nguồn
    source.balance -= transferAmount;

    // Kiểm tra số dư âm -> Ném lỗi để kích hoạt Rollback
    if (source.balance < 0) {
      throw new Error('Insufficient funds (Số dư không đủ - Đã tự động Rollback!)');
    }

    // Cộng tiền tài khoản đích
    destination.balance += transferAmount;

    return res.status(200).json({ success: true, contacts });
  } catch (error) {
    // Hoàn tác (Rollback) dữ liệu về trạng thái ban đầu
    contacts = snapshot;
    return res.status(400).json({ error: error.message });
  }
});

module.exports = app;
