import { useEffect, useState } from "react";
import { api, money } from "../api";
import { Page, Status } from "../components/UI";
export default function Dashboard() {
  const [d, setD] = useState({ upcoming: [] });
  useEffect(() => {
    api("/dashboard").then(setD);
  }, []);
  return (
    <Page
      title="Good morning"
      subtitle={`Here’s what’s happening at Sunrise today, ${new Date().toLocaleDateString("en-LK", { weekday: "long", month: "long", day: "numeric" })}.`}
    >
      <div className="metric-grid">
        {[
          [
            "calendar2-check",
            "Appointments today",
            d.appointmentsToday,
            "coral",
          ],
          ["hourglass-split", "Waiting", d.scheduled, "amber"],
          ["people", "Total patients", d.patients, "teal"],
          ["cash-stack", "Paid revenue", money(d.paidRevenue), "green"],
        ].map((x) => (
          <div className="metric" key={x[1]}>
            <i className={`bi bi-${x[0]} ${x[3]}`} />
            <span>{x[1]}</span>
            <strong>{x[2] ?? "—"}</strong>
          </div>
        ))}
      </div>
      <div className="card panel">
        <div className="panel-title">
          <h2>Today's upcoming appointments</h2>
          <a href="/appointments">
            View schedule <i className="bi bi-arrow-right" />
          </a>
        </div>
        {d.upcoming?.length ? (
          <div className="table-responsive">
            <table className="table">
              <thead>
                <tr>
                  <th>Time</th>
                  <th>Patient</th>
                  <th>Dentist</th>
                  <th>Appointment no.</th>
                  <th>Status</th>
                </tr>
              </thead>
              <tbody>
                {d.upcoming.map((a) => (
                  <tr key={a.id}>
                    <td>
                      <strong>
                        {new Date(a.appointmentTime).toLocaleTimeString([], {
                          hour: "2-digit",
                          minute: "2-digit",
                        })}
                      </strong>
                    </td>
                    <td>{a.patient.fullName}</td>
                    <td>{a.dentist.fullName}</td>
                    <td>{a.appointmentNumber}</td>
                    <td>
                      <Status value={a.status} />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="empty">
            <i className="bi bi-calendar2-heart" />
            <p>No more appointments today.</p>
          </div>
        )}
      </div>
    </Page>
  );
}
