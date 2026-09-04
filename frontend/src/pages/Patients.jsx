import { useEffect, useState } from "react";
import { api } from "../api";
import { Alert, Field, Page } from "../components/UI";
const blank = {
  fullName: "",
  dateOfBirth: "",
  gender: "",
  phone: "",
  email: "",
  address: "",
  medicalNotes: "",
};
export default function Patients() {
  const [list, setList] = useState([]),
    [q, setQ] = useState(""),
    [show, setShow] = useState(false),
    [form, setForm] = useState(blank),
    [error, setError] = useState(),
    [success, setSuccess] = useState();
  const load = () =>
    api("/patients" + (q ? `?q=${encodeURIComponent(q)}` : "")).then(setList);
  useEffect(() => {
    load();
  }, [q]);
  async function save(e) {
    e.preventDefault();
    setError();
    setSuccess();
    try {
      const saved = await api("/patients" + (form.id ? `/${form.id}` : ""), {
        method: form.id ? "PUT" : "POST",
        body: JSON.stringify({
          ...form,
          dateOfBirth: form.dateOfBirth || null,
        }),
      });
      setShow(false);
      setForm(blank);
      setSuccess(
        `Patient saved successfully. Patient ID: #${saved.id}. Create the appointment from the Appointments page.`,
      );
      load();
    } catch (x) {
      setError(x);
    }
  }
  return (
    <Page
      title="Patients"
      subtitle="Register patients and keep their contact and medical details up to date."
      action={
        <button
          className="btn btn-primary"
          onClick={() => {
            setForm(blank);
            setShow(true);
          }}
        >
          <i className="bi bi-person-plus" /> Add patient
        </button>
      }
    >
      <Alert success={success} />
      <div className="card panel">
        <div className="search">
          <i className="bi bi-search" />
          <input
            value={q}
            onChange={(e) => setQ(e.target.value)}
            placeholder="Search by patient name or phone…"
          />
        </div>
        <div className="table-responsive">
          <table className="table">
            <thead>
              <tr>
                <th>Patient</th>
                <th>Phone</th>
                <th>Email</th>
                <th>Date of birth</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {list.map((p) => (
                <tr key={p.id}>
                  <td>
                    <strong>{p.fullName}</strong>
                    <small className="d-block text-muted">ID #{p.id}</small>
                  </td>
                  <td>{p.phone}</td>
                  <td>{p.email || "—"}</td>
                  <td>{p.dateOfBirth || "—"}</td>
                  <td>
                    <button
                      className="btn btn-sm btn-light"
                      onClick={() => {
                        setForm({ ...p, dateOfBirth: p.dateOfBirth || "" });
                        setShow(true);
                      }}
                    >
                      Edit
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
      {show && (
        <div className="modal-bg">
          <form className="modal-card" onSubmit={save}>
            <div className="modal-head">
              <h2>{form.id ? "Edit" : "Register"} patient</h2>
              <button type="button" onClick={() => setShow(false)}>
                ×
              </button>
            </div>
            <Alert error={error} />
            <div className="form-grid">
              <Field label="Full name" required error={error?.fields?.fullName}>
                <input
                  className="form-control"
                  value={form.fullName}
                  onChange={(e) =>
                    setForm({ ...form, fullName: e.target.value })
                  }
                  required
                />
              </Field>
              <Field label="Phone" required>
                <input
                  className="form-control"
                  value={form.phone}
                  onChange={(e) => setForm({ ...form, phone: e.target.value })}
                  required
                />
              </Field>
              <Field label="Date of birth">
                <input
                  type="date"
                  className="form-control"
                  value={form.dateOfBirth}
                  onChange={(e) =>
                    setForm({ ...form, dateOfBirth: e.target.value })
                  }
                />
              </Field>
              <Field label="Gender">
                <select
                  className="form-select"
                  value={form.gender || ""}
                  onChange={(e) => setForm({ ...form, gender: e.target.value })}
                >
                  <option value="">Select</option>
                  <option>Female</option>
                  <option>Male</option>
                  <option>Other</option>
                </select>
              </Field>
              <Field label="Email">
                <input
                  type="email"
                  className="form-control"
                  value={form.email || ""}
                  onChange={(e) => setForm({ ...form, email: e.target.value })}
                />
              </Field>
              <Field label="Address">
                <input
                  className="form-control"
                  value={form.address || ""}
                  onChange={(e) =>
                    setForm({ ...form, address: e.target.value })
                  }
                />
              </Field>
            </div>
            <Field label="Medical notes">
              <textarea
                className="form-control"
                rows="3"
                value={form.medicalNotes || ""}
                onChange={(e) =>
                  setForm({ ...form, medicalNotes: e.target.value })
                }
              />
            </Field>
            <div className="modal-actions">
              <button
                type="button"
                className="btn btn-light"
                onClick={() => setShow(false)}
              >
                Cancel
              </button>
              <button className="btn btn-primary">Save patient</button>
            </div>
          </form>
        </div>
      )}
    </Page>
  );
}
