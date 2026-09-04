import { useEffect, useState } from "react";
import { api } from "../api";
import { Alert, Field, Page, Status } from "../components/UI";
import AppointmentLookup from "../components/AppointmentLookup";
import PatientPicker from "../components/PatientPicker";
const today = () => new Date().toISOString().slice(0, 10),
  blank = {
    patientId: "",
    dentistId: "",
    appointmentTime: "",
    durationMinutes: 30,
    notes: "",
    treatmentIds: [],
  };
export default function Appointments() {
  const [date, setDate] = useState(today()),
    [list, setList] = useState([]),
    [patients, setPatients] = useState([]),
    [dentists, setDentists] = useState([]),
    [treatments, setTreatments] = useState([]),
    [show, setShow] = useState(false),
    [form, setForm] = useState(blank),
    [error, setError] = useState(),
    [success, setSuccess] = useState();
  const load = () => api(`/appointments?date=${date}`).then(setList);
  useEffect(() => {
    load();
  }, [date]);
  useEffect(() => {
    Promise.all([api("/patients"), api("/dentists"), api("/treatments")]).then(
      ([p, d, t]) => {
        setPatients(p);
        setDentists(d.filter((x) => x.active));
        setTreatments(t.filter((x) => x.active));
      },
    );
  }, []);
  async function save(e) {
    e.preventDefault();
    setError();
    setSuccess();
    try {
      const saved = await api(
        "/appointments" +
          (form.appointmentNumber ? `/${form.appointmentNumber}` : ""),
        {
          method: form.appointmentNumber ? "PUT" : "POST",
          body: JSON.stringify({
            ...form,
            patientId: +form.patientId,
            dentistId: +form.dentistId,
            durationMinutes: +form.durationMinutes,
            treatmentIds: form.treatmentIds.map(Number),
          }),
        },
      );
      setShow(false);
      setForm(blank);
      setSuccess(
        `Appointment created successfully. Appointment number: ${saved.appointmentNumber}`,
      );
      load();
    } catch (x) {
      setError(x);
    }
  }
  async function action(n, type) {
    if (type === "cancel" && !confirm("Cancel this appointment?")) return;
    await api(`/appointments/${n}/${type}`, { method: "PATCH" });
    load();
  }
  function edit(a) {
    setForm({
      appointmentNumber: a.appointmentNumber,
      patientId: a.patient.id,
      dentistId: a.dentist.id,
      appointmentTime: a.appointmentTime.slice(0, 16),
      durationMinutes: a.durationMinutes,
      notes: a.notes || "",
      treatmentIds: a.treatments.map((t) => t.id),
    });
    setShow(true);
  }
  return (
    <Page
      title="Appointments"
      subtitle="Plan the day, find bookings, and keep every chair on schedule."
      action={
        <button
          className="btn btn-primary"
          onClick={() => {
            setForm(blank);
            setShow(true);
          }}
        >
          <i className="bi bi-plus-lg" /> New appointment
        </button>
      }
    >
      <Alert success={success} />
      <AppointmentLookup />
      <div className="card panel">
        <div className="toolbar">
          <label>
            Date{" "}
            <input
              type="date"
              className="form-control"
              value={date}
              onChange={(e) => setDate(e.target.value)}
            />
          </label>
          <span>
            {list.length} appointment{list.length !== 1 ? "s" : ""}
          </span>
        </div>
        <div className="table-responsive">
          <table className="table">
            <thead>
              <tr>
                <th>Time</th>
                <th>Appointment</th>
                <th>Patient</th>
                <th>Dentist</th>
                <th>Status</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {list.map((a) => (
                <tr key={a.id}>
                  <td>
                    <strong>
                      {new Date(a.appointmentTime).toLocaleTimeString([], {
                        hour: "2-digit",
                        minute: "2-digit",
                      })}
                    </strong>
                    <small className="d-block text-muted">
                      {a.durationMinutes} min
                    </small>
                  </td>
                  <td>{a.appointmentNumber}</td>
                  <td>
                    <strong>{a.patient.fullName}</strong>
                    <small className="d-block text-muted">
                      {a.patient.phone}
                    </small>
                  </td>
                  <td>{a.dentist.fullName}</td>
                  <td>
                    <Status value={a.status} />
                  </td>
                  <td className="text-end">
                    {a.status === "SCHEDULED" && (
                      <>
                        <button
                          className="btn btn-sm btn-light me-1"
                          onClick={() => edit(a)}
                        >
                          Edit
                        </button>
                        <button
                          className="btn btn-sm btn-outline-success me-1"
                          onClick={() =>
                            action(a.appointmentNumber, "complete")
                          }
                        >
                          Complete
                        </button>
                        <button
                          className="btn btn-sm btn-outline-danger"
                          onClick={() => action(a.appointmentNumber, "cancel")}
                        >
                          Cancel
                        </button>
                      </>
                    )}
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
              <h2>{form.appointmentNumber ? "Edit" : "New"} appointment</h2>
              <button type="button" onClick={() => setShow(false)}>
                ×
              </button>
            </div>
            <Alert error={error} />
            <div className="form-grid">
              <PatientPicker
                patients={patients}
                value={form.patientId}
                onChange={(patientId) => setForm({ ...form, patientId })}
                onCreated={(patient) =>
                  setPatients((current) => [...current, patient])
                }
              />
              <Field label="Dentist" required>
                <select
                  className="form-select"
                  value={form.dentistId}
                  onChange={(e) =>
                    setForm({ ...form, dentistId: e.target.value })
                  }
                  required
                >
                  <option value="">Select dentist</option>
                  {dentists.map((d) => (
                    <option value={d.id} key={d.id}>
                      {d.fullName}
                    </option>
                  ))}
                </select>
              </Field>
              <Field label="Date and time" required>
                <input
                  type="datetime-local"
                  className="form-control"
                  min={new Date().toISOString().slice(0, 16)}
                  value={form.appointmentTime}
                  onChange={(e) =>
                    setForm({ ...form, appointmentTime: e.target.value })
                  }
                  required
                />
              </Field>
              <Field label="Duration (minutes)">
                <input
                  type="number"
                  min="15"
                  step="15"
                  className="form-control"
                  value={form.durationMinutes}
                  onChange={(e) =>
                    setForm({ ...form, durationMinutes: e.target.value })
                  }
                />
              </Field>
            </div>
            <Field label="Treatments">
              <select
                multiple
                className="form-select treatment-select"
                value={form.treatmentIds}
                onChange={(e) =>
                  setForm({
                    ...form,
                    treatmentIds: [...e.target.selectedOptions].map(
                      (o) => o.value,
                    ),
                  })
                }
              >
                {treatments.map((t) => (
                  <option value={t.id} key={t.id}>
                    {t.name}
                  </option>
                ))}
              </select>
              <small className="text-muted">
                Hold Ctrl/Cmd to select more than one.
              </small>
            </Field>
            <Field label="Notes">
              <textarea
                className="form-control"
                value={form.notes}
                onChange={(e) => setForm({ ...form, notes: e.target.value })}
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
              <button className="btn btn-primary">Save appointment</button>
            </div>
          </form>
        </div>
      )}
    </Page>
  );
}
