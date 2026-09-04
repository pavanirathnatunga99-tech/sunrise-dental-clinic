export function Page({title,subtitle,action,children}){return <><div className="page-head"><div><h1>{title}</h1>{subtitle&&<p>{subtitle}</p>}</div>{action}</div>{children}</>}
export function Alert({error,success}){if(!error&&!success)return null;return <div className={`alert ${error?'alert-danger':'alert-success'} d-flex gap-2`}><i className={`bi ${error?'bi-exclamation-circle':'bi-check-circle'}`}/><span>{error?.message||error||success}</span></div>}
export function Empty({icon='inbox',text}){return <div className="empty"><i className={`bi bi-${icon}`}/><p>{text}</p></div>}
export function Field({label,error,children,required}){return <label className="form-label-wrap"><span>{label}{required&&<b> *</b>}</span>{children}{error&&<small className="text-danger">{error}</small>}</label>}
export function Status({value}){return <span className={`status status-${value?.toLowerCase()}`}>{value}</span>}
