package payroll;

public interface PayrollInterface
{
	double LOWER_TAX_RATE = 0.2;
	double HIGHER_TAX_RATE = 0.41;
	double USC1 = 0.02;
	double USC2 = 0.04;
	double USC3 = 0.07;
	double USCLevel1 = 10000;
	double USCLevel2 = 16000;

	double calcGrossPay(double d); 
	double calcTax(double pay, double cutOff, double credits);
	double calcPrsi(double pay);
	double calcUsc(double pay);
}
