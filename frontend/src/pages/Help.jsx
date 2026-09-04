import{useState}from'react';
import{Page}from'../components/UI';

const items=[
 ['How do I register a patient?','Open Patients, choose Add patient, enter the required name and phone number, then save.'],
 ['How do I avoid double-booking?','Choose the dentist, date, time, and duration. Sunrise checks the full time range and will explain if the dentist is already booked.'],
 ['How do I edit or cancel a booking?','Open Appointments, select its date, then use Edit or Cancel. Cancelled bookings remain visible for audit history.'],
 ['How is a bill calculated?','Open Billing, select the saved appointment, and choose Create bill. The total combines the dentist consultation fee with every selected treatment.'],
 ['How do I print a receipt?','Generate the bill and choose Print receipt. If the in-app browser blocks printing, use Save receipt, open the saved file in Chrome or Edge, and press Ctrl + P.'],
 ['Where can I find an appointment number?','Open Appointments and select the appointment date. The full number appears in the Appointment column and can also be selected directly in Billing.'],
 ['Why can’t I manage dentists or treatments?','Only an administrator can change dentist and treatment records. Ask the clinic administrator if your account needs additional access.']
];

export default function Help(){
 const[open,setOpen]=useState(0);
 return <Page title="Help centre" subtitle="Quick answers for the Sunrise clinic team."><div className="help-layout"><div className="card panel"><h2>Common questions</h2><div className="faq-list mt-3">{items.map((item,index)=>{const active=open===index;return <div className={`faq-item ${active?'open':''}`} key={item[0]}><button type="button" className="faq-question" aria-expanded={active} onClick={()=>setOpen(active?-1:index)}><span>{item[0]}</span><i className={`bi bi-chevron-${active?'up':'down'}`}/></button>{active&&<div className="faq-answer">{item[1]}</div>}</div>})}</div></div><div className="support-card"><i className="bi bi-headset"/><h2>Need more help?</h2><p>Contact the clinic administrator for account access or data corrections.</p><strong>011 234 5678</strong><span>support@sunrisedental.lk</span></div></div></Page>
}
