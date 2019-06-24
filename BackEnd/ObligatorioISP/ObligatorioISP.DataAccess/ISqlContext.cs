using System.Collections.Generic;

namespace ObligatorioISP.DataAccess
{
    public interface ISqlContext
    {
        void ExcecuteCommand(string command);

        ICollection<Dictionary<string, object>> ExcecuteRead(string query);
    }
}
