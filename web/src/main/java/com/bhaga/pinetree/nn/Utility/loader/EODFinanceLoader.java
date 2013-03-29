/*
 * EOD Finance Loader Author:  Ajay Bhaga
 */
package com.bhaga.pinetree.nn.Utility.loader;

import com.bhaga.pinetree.nn.Utility.FileUtils;
import com.bhaga.pinetree.nn.exception.NoDataException;
import com.bhaga.pinetree.nn.exception.NotLoggedInException;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import org.encog.ml.data.market.MarketDataType;
import org.encog.ml.data.market.TickerSymbol;
import org.encog.ml.data.market.loader.LoadedMarketData;

public class EODFinanceLoader {

    private final String dataWS = "http://ws.eoddata.com/data.asmx";
    private final String loginTokenFilename = "login.token";
    private String loginToken;

    public EODFinanceLoader() throws SOAPException, IOException {
        wsLogin(false);
    }

    private void wsLogin(boolean deleteToken) throws SOAPException, IOException {
        System.out.println("Attempting to login to web service...");
        // Attempt to load existing token
        File loginTokenFile = new File(loginTokenFilename);

        if (deleteToken) {
            // Delete token and generate a new one
            loginTokenFile.delete();
        }

        try {
            loginToken = FileUtils.getContents(loginTokenFile, false);
            System.out.println("Using existing login token: " + loginToken);
            return;
        } catch (IOException e) {
            System.out.println("Retrieving new login token...");
        }

        SOAPConnectionFactory soapConnectionFactory =
                SOAPConnectionFactory.newInstance();
        SOAPConnection connection =
                soapConnectionFactory.createConnection();

        MessageFactory factory =
                MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();

        SOAPHeader header = message.getSOAPHeader();
        SOAPBody body = message.getSOAPBody();
        //header.detachNode();

        MimeHeaders mimeHeader = message.getMimeHeaders();
        // Change header's attribute
        mimeHeader.setHeader("SOAPAction", "http://ws.eoddata.com/Data/Login");

        QName bodyQName = new QName("http://ws.eoddata.com/Data",
                "Login");
        SOAPBodyElement bodyElement =
                body.addBodyElement(bodyQName);

        QName usernameQName = new QName("Username");
        SOAPElement username =
                bodyElement.addChildElement(usernameQName);
        username.addTextNode("ajay.bhaga");

        QName passwordQName = new QName("Password");
        SOAPElement password =
                bodyElement.addChildElement(passwordQName);
        password.addTextNode("guju1984");

        //Check the input
        System.out.println("\nREQUEST:\n");
        message.writeTo(System.out);
        System.out.println();

        URL endpoint = new URL(dataWS);
        SOAPMessage response =
                connection.call(message, endpoint);

        connection.close();

        SOAPBody soapBody = response.getSOAPBody();

        System.out.println("\nRESPONSE:\n");
        response.writeTo(System.out);
        System.out.println();

        SOAPBody responseBody = response.getSOAPBody();
        SOAPBodyElement responseElement = (SOAPBodyElement) responseBody.getChildElements().next();
        SOAPElement returnElement = (SOAPElement) responseElement.getChildElements().next();

        // Store login token
        loginToken = returnElement.getAttribute("Token");

        // Store token
        FileUtils.setContents(loginTokenFile, loginToken);
    }

    private Collection<LoadedMarketData> getData(final TickerSymbol ticker, Date from, Date to, int stepSize) throws SOAPException, MalformedURLException, IOException, ParseException, NoDataException, NotLoggedInException {

        final Collection<LoadedMarketData> result = new ArrayList<LoadedMarketData>();

        // Convert to calendar objects
        final Calendar calendarFrom = Calendar.getInstance();
        calendarFrom.setTime(from);
        final Calendar calendarTo = Calendar.getInstance();
        calendarTo.setTime(to);

        SOAPConnectionFactory soapConnectionFactory =
                SOAPConnectionFactory.newInstance();
        SOAPConnection connection =
                soapConnectionFactory.createConnection();

        MessageFactory factory =
                MessageFactory.newInstance();
        SOAPMessage message = factory.createMessage();

        SOAPHeader header = message.getSOAPHeader();
        SOAPBody body = message.getSOAPBody();
        //header.detachNode();

        MimeHeaders mimeHeader = message.getMimeHeaders();
        // Change header's attribute
        mimeHeader.setHeader("SOAPAction", "http://ws.eoddata.com/Data/SymbolHistoryPeriodByDateRange");

        QName bodyQName = new QName("http://ws.eoddata.com/Data",
                "SymbolHistoryPeriodByDateRange");
        SOAPBodyElement bodyElement =
                body.addBodyElement(bodyQName);

        QName tokenQName = new QName("Token");
        SOAPElement token =
                bodyElement.addChildElement(tokenQName);
        token.addTextNode(loginToken);

        QName exchangeQName = new QName("Exchange");
        SOAPElement exchange =
                bodyElement.addChildElement(exchangeQName);
        exchange.addTextNode(ticker.getExchange());

        QName symbolQName = new QName("Symbol");
        SOAPElement symbol =
                bodyElement.addChildElement(symbolQName);
        symbol.addTextNode(ticker.getSymbol());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

        String startDateStr = sdf.format(from);
        String endDateStr = sdf.format(to);

        QName startDateQName = new QName("StartDate");
        SOAPElement startDate =
                bodyElement.addChildElement(startDateQName);
        startDate.addTextNode(startDateStr);

        QName endDateQName = new QName("EndDate");
        SOAPElement endDate =
                bodyElement.addChildElement(endDateQName);
        endDate.addTextNode(endDateStr);

        QName periodQName = new QName("Period");
        SOAPElement period =
                bodyElement.addChildElement(periodQName);

        switch (stepSize) {
            case Calendar.DATE:
                period.addTextNode("d");
                break;
            case Calendar.HOUR_OF_DAY:
                period.addTextNode("h");
                break;
        }

        //Check the input
        //System.out.println("\nREQUEST:\n");
        //message.writeTo(System.out);
        //System.out.println();

        URL endpoint = new URL(dataWS);
        SOAPMessage response =
                connection.call(message, endpoint);

        connection.close();

        SOAPBody soapBody = response.getSOAPBody();

        System.out.println("\nRESPONSE:\n");
        response.writeTo(System.out);
        System.out.println();

        SOAPBody responseBody = response.getSOAPBody();
        SOAPBodyElement responseElement = (SOAPBodyElement) responseBody.getChildElements().next();
        SOAPElement returnElement = (SOAPElement) responseElement.getChildElements().next();

        if (returnElement.hasAttribute("Message")) {
            if (returnElement.getAttribute("Message").equals("Not logged in")) {
                throw new NotLoggedInException("Not logged in");
            }
        }

        SOAPElement quotesElement = (SOAPElement) returnElement.getChildElements().next();
        Iterator it = quotesElement.getChildElements();

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        while (it.hasNext()) {
            SOAPElement quoteElement = (SOAPElement) it.next();
            String symbolStr = quoteElement.getAttribute("Symbol");
            String dateTimeStr = quoteElement.getAttribute("DateTime");
            String openStr = quoteElement.getAttribute("Open");
            String highStr = quoteElement.getAttribute("High");
            String lowStr = quoteElement.getAttribute("Low");
            String closeStr = quoteElement.getAttribute("Close");
            String volumeStr = quoteElement.getAttribute("Volume");

            Date date = sdf2.parse(dateTimeStr);
            //DateTime="2012-09-21T00:00:00"
            double openValue = Double.parseDouble(openStr);
            double highValue = Double.parseDouble(highStr);
            double lowValue = Double.parseDouble(lowStr);
            double closeValue = Double.parseDouble(closeStr);
            double volumeValue = Double.parseDouble(volumeStr);

            final LoadedMarketData data =
                    new LoadedMarketData(date, ticker);
            data.setData(MarketDataType.OPEN, openValue);
            data.setData(MarketDataType.CLOSE, closeValue);
            data.setData(MarketDataType.HIGH, highValue);
            data.setData(MarketDataType.LOW, lowValue);
            data.setData(MarketDataType.VOLUME, volumeValue);

            // Add data to data set
            result.add(data);

            String str = String.format("Date: %s, Symbol: %s, Open: %s, High: %s, Low: %s, Close: %s, Volume: %s.", dateTimeStr, symbolStr, openStr, highStr, lowStr, closeStr, volumeStr);
            System.out.println(str);
        }

        System.out.println("Data Set Returned Size: " + result.size());

        if (result.isEmpty()) {
            System.out.println("Invalid exchange/symbol code!");
            throw new NoDataException("Invalid exchange/symbol code!");
        }

        // Return loaded list
        return result;
    }

    /**
     * Load the specified financial data.
     *
     * @param ticker The ticker symbol to load.
     * @param dataNeeded The financial data needed.
     * @param from The beginning date to load data from.
     * @param to The ending date to load data to.
     * @return A collection of LoadedMarketData objects that represent the data
     * loaded.
     */
    public Collection<LoadedMarketData> load(final TickerSymbol ticker,
            final Set<MarketDataType> dataNeeded, final Date from,
            final Date to) throws SOAPException, MalformedURLException, IOException, ParseException, NoDataException, NotLoggedInException {
        // Default to day
        return load(ticker, dataNeeded, from, to, Calendar.DATE);
    }

    /**
     * Load the specified financial data.
     *
     * @param ticker The ticker symbol to load.
     * @param dataNeeded The financial data needed.
     * @param from The beginning date to load data from.
     * @param to The ending date to load data to.
     * @return A collection of LoadedMarketData objects that represent the data
     * loaded.
     */
    public Collection<LoadedMarketData> load(final TickerSymbol ticker,
            final Set<MarketDataType> dataNeeded, final Date from,
            final Date to, final int stepSize) throws SOAPException, MalformedURLException, IOException, ParseException, NoDataException {
        try {
            return this.getData(ticker, from, to, stepSize);
        } catch (NotLoggedInException ex) {
            // Login again and retry
            wsLogin(true);
            try {
                return this.getData(ticker, from, to, stepSize);
            } catch (NotLoggedInException ex1) {
                System.out.println(ex1.getMessage());
                System.exit(1);
            }
        }
        
        return null;
    }
}
