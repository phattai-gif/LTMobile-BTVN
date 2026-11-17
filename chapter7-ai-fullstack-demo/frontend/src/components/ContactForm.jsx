import React, { useState } from 'react';

/**
 * Component: ContactForm
 * Ý 2 trong sách: Tách nhỏ giao diện thành component tái sử dụng.
 * Ý 3 trong sách: Nhận prop `onAddContact` từ Component cha (App).
 */
export default function ContactForm({ onAddContact }) {
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();
    onAddContact({ name, email }, () => {
      // Callback reset form khi thêm thành công
      setName('');
      setEmail('');
    });
  };

  return (
    <div className="card">
      <div className="card-title">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"></path>
          <circle cx="9" cy="7" r="4"></circle>
          <line x1="19" y1="8" x2="19" y2="14"></line>
          <line x1="22" y1="11" x2="16" y2="11"></line>
        </svg>
        Thêm liên lạc mới
      </div>
      <div className="card-subtitle">Server-side Validation tự động kiểm tra họ tên & email</div>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label className="form-label">Họ và tên</label>
          <input
            type="text"
            className="form-input"
            placeholder="Nhập tên thành viên..."
            value={name}
            onChange={e => setName(e.target.value)}
          />
        </div>
        <div className="form-group">
          <label className="form-label">Địa chỉ Email</label>
          <input
            type="text"
            className="form-input"
            placeholder="name@example.com"
            value={email}
            onChange={e => setEmail(e.target.value)}
          />
        </div>
        <button type="submit" className="btn-primary">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <line x1="12" y1="5" x2="12" y2="19"></line>
            <line x1="5" y1="12" x2="19" y2="12"></line>
          </svg>
          Tạo thành viên
        </button>
      </form>
    </div>
  );
}
