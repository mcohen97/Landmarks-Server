using System.Threading.Tasks;

namespace ObligatorioISP.Services.Contracts
{
    public interface IProximityNotificationService
    {
        Task<bool> NotifyIfCloseToLandmark(string token, double lat, double lng);
    }
}
