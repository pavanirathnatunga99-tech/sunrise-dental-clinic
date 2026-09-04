import { useEffect, useState } from "react";
import { api, money } from "../api";
import { Alert, Field, Page } from "../components/UI";
export default function Manage({ type }) {
  const dentist = type === "dentists",
    blank = dentist
      ? { fullName: "", specialization: "", consultationFee: "", active: true }
      : { code: "", name: "", cost: "", active: true },
    [list, setList] = useState([]),
    [form, setForm] = useState(blank),
    [show, setShow] = useState(false),
    [error, setError] = useState();
  const load = () => api("/" + type).then(setList);
  useEffect(() => {
    load();
  }, [type]);
  async function save(e) {
    e.preventDefault();
    setError();
    try {
      await api(`/${type}${form.id ? `/${form.id}` : ""}`, {
        method: form.id ? "PUT" : "POST",
        body: JSON.stringify(form),
      });
      setShow(false);
      setForm(blank);
      load();
    } catch (x) {
      setError(x);
    }
  }
  const title = dentist ? "Dentists" : "Treatments";
  return (
    <Page
      title={title}
      subtitle={`Manage clinic ${type} and ${dentist ? "consultation fees" : "standard pricing"}.`}
      action={
        <button
          className="btn btn-primary"
          onClick={() => {
            setForm(blank);
            setShow(true);
          }}
        >
          <i className="bi bi-plus-lg" /> Add{" "}
          {dentist ? "dentist" : "treatment"}
        </button>
      }
    >
      <div className="card panel">
        <table className="table">
          <thead>
            <tr>
              {!dentist && <th>Code</th>}
              <th>Name</th>
              <th>{dentist ? "Specialization" : "Price"}</th>
              {dentist && <th>Fee</th>}
              <th>Status</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {list.map((x) => (
              <tr key={x.id}>
                {!dentist && (
                  <td>
                    <code>{x.code}</code>
                  </td>
                )}
                <td>
                  <strong>{x.fullName || x.name}</strong>
                </td>
                <td>{dentist ? x.specialization : money(x.cost)}</td>
                {dentist && <td>{money(x.consultationFee)}</td>}
                <td>{x.active ? "Active" : "Inactive"}</td>
                <td>
                  <button
                    className="btn btn-sm btn-light"
                    onClick={() => {
                      setForm(x);
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
      {show && (
        <div className="modal-bg">
          <form className="modal-card compact" onSubmit={save}>
            <div className="modal-head">
              <h2>
                {form.id ? "Edit" : "Add"} {dentist ? "dentist" : "treatment"}
              </h2>
              <button type="button" onClick={() => setShow(false)}>
                ×
              </button>
            </div>
            <Alert error={error} />
            {dentist ? (
              <>
                <Field label="Full name" required>
                  <input
                    className="form-control"
                    value={form.fullName}
                    onChange={(e) =>
                      setForm({ ...form, fullName: e.target.value })
                    }
                    required
                  />
                </Field>
                <Field label="Specialization" required>
                  <input
                    className="form-control"
                    value={form.specialization}
                    onChange={(e) =>
                      setForm({ ...form, specialization: e.target.value })
                    }
                    required
                  />
                </Field>
                <Field label="Consultation fee (LKR)" required>
                  <input
                    type="number"
                    min="0"
                    className="form-control"
                    value={form.consultationFee}
                    onChange={(e) =>
                      setForm({ ...form, consultationFee: e.target.value })
                    }
                    required
                  />
                </Field>
              </>
            ) : (
              <>
                <Field label="Code" required>
                  <input
                    className="form-control"
                    value={form.code}
                    onChange={(e) => setForm({ ...form, code: e.target.value })}
                    required
                  />
                </Field>
                <Field label="Treatment name" required>
                  <input
                    className="form-control"
                    value={form.name}
                    onChange={(e) => setForm({ ...form, name: e.target.value })}
                    required
                  />
                </Field>
                <Field label="Cost (LKR)" required>
                  <input
                    type="number"
                    min="0"
                    className="form-control"
                    value={form.cost}
                    onChange={(e) => setForm({ ...form, cost: e.target.value })}
                    required
                  />
                </Field>
              </>
            )}
            <label className="form-check">
              <input
                className="form-check-input"
                type="checkbox"
                checked={form.active}
                onChange={(e) => setForm({ ...form, active: e.target.checked })}
              />{" "}
              Active
            </label>
            <div className="modal-actions">
              <button
                type="button"
                className="btn btn-light"
                onClick={() => setShow(false)}
              >
                Cancel
              </button>
              <button className="btn btn-primary">Save</button>
            </div>
          </form>
        </div>
      )}
    </Page>
  );
}
