import React from 'react';

/**
 * Component: ContactList
 * Ý 2 trong sách: Tách nhỏ giao diện thành component hiển thị danh sách.
 * Ý 3 trong sách: Nhận props đồng bộ kiểu dữ liệu:
 *   - contacts: Array<{ id: number, name: string, email: string, balance: number }>
 *   - isLoading: boolean
 *   - onDelete: (id: number, name: string) => void
 */
export default function ContactList({ contacts, isLoading, onDelete }) {
  const getInitials = (fullName) => {
    if (!fullName) return '?';
    const parts = fullName.trim().split(' ');
    if (parts.length === 1) return parts[0].substring(0, 2).toUpperCase();
    return (parts[0][0] + parts[parts.length - 1][0]).toUpperCase();
  };

  return (
    <div className="card">
      <div className="card-title">
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
          <path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path>
          <circle cx="9" cy="7" r="4"></circle>
          <path d="M23 21v-2a4 4 0 0 0-3-3.87"></path>
          <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
        </svg>
        Danh sách thành viên & Ví tiền
      </div>
      <div className="card-subtitle">
        Dữ liệu đồng bộ trực tiếp từ REST API Backend qua cổng 5000
      </div>

      {isLoading ? (
        <div className="empty-state">Đang tải dữ liệu từ API...</div>
      ) : contacts.length === 0 ? (
        <div className="empty-state">Chưa có thành viên nào. Hãy thêm thành viên đầu tiên!</div>
      ) : (
        <div className="table-container">
          <table className="contacts-table">
            <thead>
              <tr>
                <th>Thành viên</th>
                <th>Email</th>
                <th>Số dư ví</th>
                <th style={{ textAlign: 'right' }}>Thao tác</th>
              </tr>
            </thead>
            <tbody>
              {contacts.map(c => (
                <tr key={c.id}>
                  <td>
                    <div className="user-cell">
                      <div className="user-avatar">{getInitials(c.name)}</div>
                      <span className="user-name">{c.name}</span>
                    </div>
                  </td>
                  <td>
                    <span className="user-email">{c.email}</span>
                  </td>
                  <td>
                    <span className="balance-badge">${c.balance}</span>
                  </td>
                  <td style={{ textAlign: 'right' }}>
                    <button
                      className="btn-delete"
                      onClick={() => onDelete(c.id, c.name)}
                      title="Xóa thành viên này"
                    >
                      <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <polyline points="3 6 5 6 21 6"></polyline>
                        <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                      </svg>
                      Xóa
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}
