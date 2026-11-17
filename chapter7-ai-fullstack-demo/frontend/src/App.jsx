import React, { useState, useEffect } from 'react';
import './App.css';
import ContactForm from './components/ContactForm';
import ContactList from './components/ContactList';
import TransferForm from './components/TransferForm';

export default function App() {
  const [contacts, setContacts] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');
  const [isTransferring, setIsTransferring] = useState(false);

  // Toggle kịch bản thuyết trình
  const [showDemoHelper, setShowDemoHelper] = useState(false);

  /**
   * [LƯU Ý 1 TRONG SÁCH - TRANG 126]
   * "Kiểm tra kỹ mảng phụ thuộc trong useEffect để tránh lỗi gọi API lặp vô tận"
   * Mảng dependencies rỗng `[]` đảm bảo chỉ fetch dữ liệu 1 lần duy nhất khi component mount.
   */
  useEffect(() => {
    fetchContacts();
  }, []); // <-- Dependencies array rỗng [] ngăn infinite re-render loop

  const fetchContacts = () => {
    setIsLoading(true);
    fetch('/api/contacts')
      .then(res => res.json())
      .then(data => {
        setContacts(data);
        setIsLoading(false);
      })
      .catch(() => {
        setError('Không thể kết nối đến Backend Server (Port 5000)!');
        setIsLoading(false);
      });
  };

  /**
   * Xử lý thêm thành viên mới
   * Nhận dữ liệu từ ContactForm hoặc từ nút 1-click Demo
   */
  const handleAddContact = async (formData, onSuccess) => {
    setError('');
    setSuccessMsg('');

    try {
      const res = await fetch('/api/contacts', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
      });

      const data = await res.json();
      if (!res.ok) {
        // Bắt lỗi Server-side Validation (HTTP 400)
        setError(data.error);
        return;
      }

      // HTTP 201 Thành công
      setContacts(prev => [...prev, data]);
      setSuccessMsg(`Đã thêm thành công thành viên: ${data.name}`);
      if (onSuccess) onSuccess();
    } catch {
      setError('Lỗi kết nối khi gửi yêu cầu tới API.');
    }
  };

  /**
   * Xử lý xóa thành viên
   */
  const handleDelete = async (id, contactName) => {
    if (!window.confirm(`Bạn có chắc chắn muốn xóa "${contactName}"?`)) return;
    setError('');
    setSuccessMsg('');

    try {
      const res = await fetch(`/api/contacts/${id}`, { method: 'DELETE' });
      if (res.ok) {
        setContacts(prev => prev.filter(c => c.id !== id));
        setSuccessMsg(`Đã xóa "${contactName}" khỏi danh bạ.`);
      }
    } catch {
      setError('Lỗi kết nối khi xóa liên lạc.');
    }
  };

  /**
   * Xử lý chuyển tiền nguyên tử (Atomic Transaction)
   */
  const handleTransfer = async (transferPayload) => {
    setError('');
    setSuccessMsg('');

    if (transferPayload.fromId === transferPayload.toId) {
      setError('Tài khoản nguồn và tài khoản nhận không được trùng nhau!');
      return;
    }

    setIsTransferring(true);
    try {
      const res = await fetch('/api/transfer', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(transferPayload)
      });

      const data = await res.json();
      if (!res.ok) {
        // Ném lỗi và Rollback (HTTP 400)
        setError(`Thao tác bị từ chối: ${data.error}`);
      } else {
        // Thành công (HTTP 200)
        setContacts(data.contacts);
        const sender = contacts.find(c => c.id === transferPayload.fromId);
        const receiver = contacts.find(c => c.id === transferPayload.toId);
        setSuccessMsg(
          `Giao dịch thành công! Đã chuyển $${transferPayload.amount} từ "${sender?.name || 'Nguồn'}" sang "${receiver?.name || 'Đích'}".`
        );
      }
    } catch {
      setError('Không thể kết nối đến máy chủ API.');
    } finally {
      setIsTransferring(false);
    }
  };

  const totalBalance = contacts.reduce((sum, c) => sum + (c.balance || 0), 0);

  return (
    <div className="app-wrapper">
      {/* Header & Navbar */}
      <header className="app-header">
        <div className="brand-section">
          <div className="brand-icon">
            <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M21 12V7H5a2 2 0 0 1 0-4h14v4" />
              <path d="M3 5v14a2 2 0 0 0 2 2h16v-5" />
              <path d="M18 12a2 2 0 0 0 0 4h4v-4Z" />
            </svg>
          </div>
          <div>
            <div className="brand-title">
              FinPay & Contacts Manager
              <span className="badge-tag">Chapter 7 Full-Stack</span>
            </div>
            <div className="brand-subtitle">
              Kiến trúc Client - Server, Server Validation & Atomic Rollback
            </div>
          </div>
        </div>

        <div className="header-actions">
          <div className="status-pill">
            <span className="status-dot"></span>
            API: 5000 (Online)
          </div>
          <button
            className="demo-toggle-btn"
            onClick={() => setShowDemoHelper(!showDemoHelper)}
            title="Bật/Tắt thanh kịch bản thuyết trình"
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"></polygon>
            </svg>
            {showDemoHelper ? 'Ẩn kịch bản' : 'Kịch bản Demo'}
          </button>
        </div>
      </header>

      {/* Presenter Quick-Action Bar */}
      {showDemoHelper && (
        <div className="demo-banner">
          <div className="demo-banner-title">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <circle cx="12" cy="12" r="10"></circle>
              <line x1="12" y1="16" x2="12" y2="12"></line>
              <line x1="12" y1="8" x2="12.01" y2="8"></line>
            </svg>
            Phím tắt kịch bản thuyết trình (1-Click Demo)
          </div>
          <div className="demo-chips-group">
            <button
              className="demo-chip"
              onClick={() => handleAddContact({ name: '', email: 'test@utc2.edu.vn' })}
            >
              🧪 1. Test Server Validation (Gửi tên rỗng)
            </button>
            <button
              className="demo-chip"
              onClick={() => {
                if (contacts.length >= 2) {
                  handleTransfer({ fromId: contacts[0].id, toId: contacts[1].id, amount: 100 });
                }
              }}
            >
              ⚡ 2. Test Chuyển $100 hợp lệ
            </button>
            <button
              className="demo-chip rollback"
              onClick={() => {
                if (contacts.length >= 2) {
                  handleTransfer({ fromId: contacts[1].id, toId: contacts[0].id, amount: 5000 });
                }
              }}
            >
              🛡️ 3. Test Rollback $5000 (Vượt số dư)
            </button>
          </div>
        </div>
      )}

      {/* Stats Summary Bar */}
      <div className="stats-grid">
        <div className="stat-card">
          <div>
            <div className="stat-label">Tổng thành viên</div>
            <div className="stat-value">{contacts.length}</div>
          </div>
          <div className="stat-icon">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"></path>
              <circle cx="9" cy="7" r="4"></circle>
              <path d="M22 21v-2a4 4 0 0 0-3-3.87"></path>
              <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
            </svg>
          </div>
        </div>

        <div className="stat-card">
          <div>
            <div className="stat-label">Tổng số dư trong ví</div>
            <div className="stat-value" style={{ color: '#059669' }}>${totalBalance}</div>
          </div>
          <div className="stat-icon" style={{ background: '#ecfdf5', color: '#059669' }}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <line x1="12" y1="1" x2="12" y2="23"></line>
              <path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"></path>
            </svg>
          </div>
        </div>

        <div className="stat-card">
          <div>
            <div className="stat-label">Kiến trúc Component</div>
            <div className="stat-value" style={{ fontSize: '1.15rem', color: '#2563eb' }}>
              Modular & Clean Props
            </div>
          </div>
          <div className="stat-icon" style={{ background: '#eff6ff', color: '#2563eb' }}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
            </svg>
          </div>
        </div>
      </div>

      {/* Notifications */}
      {error && (
        <div className="notification error">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <circle cx="12" cy="12" r="10"></circle>
            <line x1="12" y1="8" x2="12" y2="12"></line>
            <line x1="12" y1="16" x2="12.01" y2="16"></line>
          </svg>
          <div>
            <strong>Thông báo từ hệ thống:</strong> {error}
          </div>
        </div>
      )}

      {successMsg && (
        <div className="notification success">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
            <polyline points="22 4 12 14.01 9 11.01"></polyline>
          </svg>
          <div>{successMsg}</div>
        </div>
      )}

      {/* [LƯU Ý 2 & 3 TRONG SÁCH - TRANG 124 & 134]
          - Tách nhỏ thành các Component con: <ContactForm />, <TransferForm />, <ContactList />
          - Đồng bộ chặt chẽ tên props và kiểu dữ liệu truyền giữa cha và con */}
      <div className="main-grid">
        <div>
          {/* Component con 1: ContactForm */}
          <ContactForm onAddContact={handleAddContact} />

          {/* Component con 2: TransferForm */}
          <TransferForm
            contacts={contacts}
            onTransfer={handleTransfer}
            isTransferring={isTransferring}
          />
        </div>

        <div>
          {/* Component con 3: ContactList */}
          <ContactList
            contacts={contacts}
            isLoading={isLoading}
            onDelete={handleDelete}
          />
        </div>
      </div>
    </div>
  );
}
